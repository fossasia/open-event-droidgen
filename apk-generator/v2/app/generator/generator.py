import json
import os
import re
import shutil
import subprocess
import urllib
import uuid

import requests
import validators
from flask import current_app

from app.utils import replace, clear_dir, unzip, get_build_tools_version
from app.utils.assets import resize_launcher_icon, resize_background_image
from app.utils.libs.asset_resizer import DENSITY_TYPES
from app.utils.notification import Notification


class Generator:
    """
    The app generator. This is where it all begins :)
    """

    def __init__(self, config, via_api=False, identifier=None, task_handle=None):
        if not identifier:
            self.identifier = str(uuid.uuid4())
        else:
            self.identifier = identifier
        self.task_handle = task_handle
        self.update_status('Starting the generator')
        self.config = config
        self.working_dir = config['WORKING_DIR']
        self.src_dir = config['APP_SOURCE_DIR']
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
        self.via_api = via_api

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
        self.update_status('Normalizing source data')
        if not endpoint_url and not zip_file:
            raise Exception('endpoint_url or zip_file is required')
        if endpoint_url:
            self.api_link = endpoint_url
            os.makedirs(self.app_temp_assets)
            event_info = requests.get(endpoint_url + '/event').json()
        else:
            unzip(zip_file, self.app_temp_assets)
            with open(self.get_temp_asset_path('/event')) as json_data:
                event_info = json.load(json_data)

        self.event_name = event_info['name']
        self.app_name = self.event_name
        self.creator_email = creator_email
        self.update_status('Processing background image and logo')
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

    def generate(self, should_notify=True):
        """
        Generate the app
        :return: the path to the generated apk
        """
        self.update_status('Preparing parent source code')

        self.prepare_source()

        self.app_package_name = 'org.fossasia.openevent.' + re.sub('\W+', '', self.app_name)

        config = {
            'Email': self.creator_email,
            'App_Name': self.app_name,
            'Api_Link': self.api_link
        }

        self.update_status('Generating app configuration')

        with open(self.get_path("app/src/main/assets/config.json"), "w+") as config_file:
            config_file.write(json.dumps(config))

        self.update_status('Generating launcher icons & background image')

        resize_launcher_icon(self.app_launcher_icon, self.app_working_dir)
        resize_background_image(self.app_background_image, self.app_working_dir)

        self.update_status('Updating resources')

        replace(self.get_path("app/src/main/res/values/strings.xml"), 'OpenEvent', self.app_name)
        replace(self.get_path("app/src/main/res/layout/nav_header.xml"), 'twitter', 'background')
        replace(self.get_path("app/build.gradle"), '"org.fossasia.openevent"', '"%s"' % self.app_package_name)

        self.update_status('Loading assets')

        for f in os.listdir(self.app_temp_assets):
            path = os.path.join(self.app_temp_assets, f)
            if os.path.isfile(path):
                shutil.copyfile(path, self.get_path("app/src/main/assets/" + f))

        self.update_status('Preparing android build tools')

        build_tools_version = get_build_tools_version(self.get_path('app/build.gradle'))
        build_tools_path = os.path.abspath(os.environ.get('ANDROID_HOME') + '/build-tools/' + build_tools_version)

        self.update_status('Building android application package')

        self.run_command([os.path.abspath(self.config['BASE_DIR'] + '/scripts/build_apk.sh'), build_tools_path])

        self.update_status('Application package generated')

        self.apk_path = self.get_path('release.apk')
        if should_notify:
            self.notify()

        apk_url = '/static/releases/%s.apk' % self.identifier

        shutil.move(self.apk_path, os.path.abspath(self.config['BASE_DIR'] + '/app/' + apk_url))

        self.update_status('SUCCESS', message=apk_url)

        self.cleanup()

        return apk_url

    def prepare_source(self):
        """
        Prepare the app-specific source based off the parent
        :return:
        """
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
        zip_file = os.path.join(self.config['UPLOAD_DIR'], self.identifier)
        if os.path.isfile(zip_file):
            os.remove(zip_file)

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
                file_attachment=apk_path,
                via_api=self.via_api
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
                file_attachment=apk_path,
                via_api=self.via_api
            )

    def update_status(self, state, exception=None, message=None):
        if self.task_handle:
            if not current_app.config.get('CELERY_ALWAYS_EAGER'):
                meta = {}
                if exception:
                    meta = {'exc': exception}
                if message:
                    meta = {'message': message}
                self.task_handle.update_state(
                    state=state, meta=meta
                )

    def run_command(self, command):
        process = subprocess.Popen(command,
                                   stdout=subprocess.PIPE,
                                   cwd=self.app_working_dir,
                                   env=os.environ.copy())
        while True:
            output = process.stdout.readline()
            if output == '' and process.poll() is not None:
                break
            if output:
                self.generate_status_updates(output.strip())
        rc = process.poll()
        return rc

    def generate_status_updates(self, output_line):
        if 'Starting process \'Gradle build daemon\'' in output_line:
            self.update_status('Starting gradle builder')
        elif 'Creating configuration' in output_line:
            self.update_status('Creating configuration')
        elif 'Parsing the SDK' in output_line:
            self.update_status('Preparing Android SDK')
        elif 'app:preBuild' in output_line:
            self.update_status('Running pre-build tasks')
        elif 'Loading library manifest' in output_line:
            self.update_status('Loading libraries')
        elif 'Merging' in output_line:
            self.update_status('Merging resources')
        elif 'intermediates' in output_line:
            self.update_status('Generating intermediates')
        elif 'is not translated' in output_line:
            self.update_status('Processing strings')
        elif 'generateFdroidReleaseAssets' in output_line:
            self.update_status('Processing strings')
        elif 'Adding PreDexTask' in output_line:
            self.update_status('Adding pre dex tasks')
        elif 'Dexing' in output_line:
            self.update_status('Dexing classes')
        elif 'packageGoogleplayRelease' in output_line:
            self.update_status('Packaging release')
        elif 'assembleRelease' in output_line:
            self.update_status('Assembling release')
        elif 'BUILD SUCCESSFUL' in output_line:
            self.update_status('Build successful. Starting the signing process.')
        elif 'signing' in output_line:
            self.update_status('Signing the package.')
        elif 'jar signed' in output_line:
            self.update_status('Package signed.')
        elif 'zipaligning' in output_line:
            self.update_status('Verifying the package.')
        elif 'Verification succesful' in output_line:
            self.update_status('Package verified.')
        elif output_line == 'done':
            self.update_status('Application has been generated. Please wait.')


