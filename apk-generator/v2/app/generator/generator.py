import os
import shutil
import uuid

from app.utils.asset_resizer import DENSITY_TYPES


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
        self.app_name = 'Open Event'
        self.app_working_dir = os.path.abspath(working_dir + '/' + src_dir)
        self.app_background_image = os.path.abspath(config['BASE_DIR'] + '/app/static/assets/background.jpg')
        self.app_launcher_icon = os.path.abspath(config['BASE_DIR'] + '/app/static/assets/ic_launcher.png')
        pass

    def normalize(self, endpoint_url=None, zip_file=None):
        """
        Normalize the required data irrespective of the source
        :param endpoint_url:
        :param zip_file:
        :return:
        """
        if not endpoint_url and not zip_file:
            raise Exception('endpoint_url or zip_file is required')

    def generate(self):
        """
        Generate the app
        :return: the path to the generated apk
        """
        self.prepare_source()

    def prepare_source(self):
        os.mkdir(self.app_working_dir)
        shutil.copytree(self.src_dir, self.app_working_dir)
        for density in DENSITY_TYPES:
            mipmap_dir = os.path.abspath(self.app_working_dir + '/app/src/main/res/mipmap-%s' % density)
            if os.path.exists(mipmap_dir):
                shutil.rmtree(mipmap_dir, True)
