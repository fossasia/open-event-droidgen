import json
import os
import re
import shutil
import subprocess
import urllib
import uuid

import requests
import validators

from app.utils import replace, clear_dir, unzip, get_build_tools_version
from app.utils.asset_resizer import DENSITY_TYPES
from app.utils.assets import resize_launcher_icon, resize_background_image
from app.utils.notification import Notification


class Generator:
    """
    The app generator. This is where it all begins :)
    """

    def __init__(self, config, working_dir, src_dir):
        self.config = config
        self.identifier = str(uuid.uuid4())
        self.working_dir = working_dir
        self.src_dir = src_dir
        self.creator_email = 'john.doe@example.com'
        self.event_name = 'Open Event'
        self.app_name = self.event_name
        self.app_working_dir = os.path.abspath(self.working_dir + '/' + self.identifier + '/android-src/')
        self.app_background_image = os.path.abspath(config['BASE_DIR'] + '/app/static/assets/background.jpg')
        self.app_launcher_icon = os.path.abspath(config['BASE_DIR'] + '/app/static/assets/ic_launcher.png')
        self.app_package_name = 'org.fossasia.openevent.' + self.app_name.replace(" ", "")
        self.app_temp_assets = os.path.abspath(self.working_dir + '/' + self.identifier + '/assets-src/')
        self.api_link = ''
        self.apk_path = ''

    def get_path(self, relative_path):
        """
        Get the path to a resource relative to the app source
        :param relative_path:
        :return:
        """
        return os.path.abspath(self.app_working_dir + '/' + relative_path)

    def get_temp_asset_path(self, relative_path):
        """
        Get the path to a resource relative to the temp assets dir
        :param relative_path:
        :return:
        """
        return os.path.abspath(self.app_temp_assets + '/' + relative_path)

    def normalize(self, creator_email, endpoint_url=None, zip_file=None):
        """
        Normalize the required data irrespective of the source
        :param creator_email:
        :param endpoint_url:
        :param zip_file:
        :return:
        """
        if not endpoint_url and not zip_file:
            raise Exception('endpoint_url or zip_file is required')
        if endpoint_url:
            self.api_link = endpoint_url
            event_info = requests.get(endpoint_url + '/event').json()
        else:
            unzip(zip_file, self.app_temp_assets)
            with open(self.get_temp_asset_path('/event')) as json_data:
                event_info = json.load(json_data)

        self.event_name = event_info['name']
        self.app_name = self.event_name
        self.creator_email = creator_email

        background_image = event_info['background_image'].strip() if event_info['background_image'] else ''
        logo = event_info['logo'].strip() if event_info['logo'] else ''
        if background_image != '':
            if background_image.startswith("/"):
                self.app_background_image = self.get_temp_asset_path(background_image)
            elif validators.url(background_image):
                self.app_background_image = self.get_temp_asset_path('background.png')
                urllib.urlretrieve(background_image, self.app_background_image)
        if logo != '':
            if logo.startswith("/"):
                self.app_launcher_icon = self.get_temp_asset_path(logo)
            elif validators.url(logo):
                self.app_launcher_icon = self.get_temp_asset_path('logo.png')
                urllib.urlretrieve(logo, self.app_launcher_icon)

    def generate(self):
        """
        Generate the app
        :return: the path to the generated apk
        """
        try:
            self.prepare_source()

            self.app_package_name = 'org.fossasia.openevent.' + re.sub('\W+', '', self.app_name)

            config = {
                'Email': self.creator_email,
                'App_Name': self.app_name,
                'Api_Link': self.api_link
            }

            with open(self.get_path("app/src/main/assets/config.json"), "w+") as config_file:
                config_file.write(json.dumps(config))

            resize_launcher_icon(self.app_launcher_icon, self.app_working_dir)
            resize_background_image(self.app_background_image, self.app_working_dir)
            replace(self.get_path("app/src/main/res/values/strings.xml"), 'OpenEvent', self.app_name)
            replace(self.get_path("app/src/main/res/layout/nav_header.xml"), 'twitter', 'background')
            replace(self.get_path("app/build.gradle"), '"org.fossasia.openevent"', '"%s"' % self.app_package_name)

            for f in os.listdir(self.app_temp_assets):
                if os.path.isfile(os.path.join(self.app_temp_assets, f)):
                    shutil.copyfile(self.app_temp_assets + f, self.get_path("app/src/main/assets/" + f))

            build_tools_version = get_build_tools_version(self.get_path('app/build.gradle'))
            build_tools_path = os.path.abspath(os.environ.get('ANDROID_HOME') + '/build-tools/' + build_tools_version)
            subprocess.check_call([os.path.abspath(config['BASE_DIR'] + '/scripts/build_apk.sh'), build_tools_path],
                                  cwd=self.app_working_dir,
                                  env=os.environ.copy())
            
            self.apk_path = self.get_path('release.apk')
            self.notify()
        except Exception as e:
            self.notify(False, e)

    def prepare_source(self):
        """
        Prepare the app-specific source based off the parent
        :return:
        """
        os.mkdir(self.app_working_dir)
        shutil.copytree(self.src_dir, self.app_working_dir)
        for density in DENSITY_TYPES:
            mipmap_dir = self.get_path("app/src/main/res/mipmap-%s" % density)
            if os.path.exists(mipmap_dir):
                shutil.rmtree(mipmap_dir, True)
        clear_dir(self.get_path("app/src/main/assets/"))

    def cleanup(self):
        """
        Clean-up after done like a good fella :)
        :return:
        """
        shutil.rmtree(os.path.abspath(self.working_dir + '/' + self.identifier + '/'))

    def notify(self, completed=True, apk_path=None, error=None):
        """
        Notify the creator of success or failure of the app generation
        :param completed:
        :param apk_path:
        :param error:
        :return:
        """
        if completed and apk_path and not error:
            Notification.send(
                to=self.creator_email,
                subject='Your android application for %s has been generated ' % self.event_name,
                message='Hi,<br><br>'
                        'Your android application for the \'%s\' event has been generated. '
                        'And apk file has been attached along with this email.<br><br>'
                        'Thanks,<br>'
                        'Open Event App Generator' % self.event_name,
                file_attachment=apk_path
            )
        else:
            Notification.send(
                to=self.creator_email,
                subject='Your android application for %s could not generated ' % self.event_name,
                message='Hi,<br><br> '
                        'Your android application for the \'%s\' event could not generated. '
                        'The error message has been provided below.<br><br>'
                        '<code>%s</code><br><br>'
                        'Thanks,<br>'
                        'Open Event App Generator' % (self.event_name, str(error) if error else ''),
                file_attachment=apk_path
            )
