import os

from PIL import Image

from app.utils.libs.asset_resizer import AssetResizer


def rename_file(file_path, new_name):
    """
    Rename a file while maintaining it's extension
    :param file_path: the path to the file
    :param new_name: new name
    :return:
    """
    path, filename = os.path.split(file_path)
    filename, file_extension = os.path.splitext(file_path)
    new_path = os.path.join(path, new_name + file_extension)
    os.rename(file_path, new_path)
    return new_path, new_name + file_extension


def resize_launcher_icon(icon_path, app_directory):
    """
    Resize a launcher icon into it's appropriate densities and save it into the app's resources directory
    :param icon_path: The path to the icon
    :param app_directory: The path to the app-level working directory
    :return:
    """
    icon_path, filename = rename_file(icon_path, 'ic_launcher')
    destination = os.path.abspath(app_directory + '/app/src/main/')
    resizer = AssetResizer(destination, directory_prefix='mipmap', image_filter=Image.ANTIALIAS)
    resizer.mkres()
    resizer.resize(icon_path)
    os.remove(icon_path)


def resize_background_image(background_path, app_directory):
    """
    Resize a background image into it's appropriate dimension and save it into the app's resources directory
    :param background_path: The path to the background image
    :param app_directory: The path to the app-level working directory
    :return:
    """
    destination = os.path.abspath(app_directory + '/app/src/main/res/drawable/')
    back = Image.open(background_path)
    back.load()
    background_resized = Image.new("RGB", back.size)
    background_resized.paste(back)
    background_resized = background_resized.resize((600, 400), Image.ANTIALIAS)
    background_resized.save(destination + "/background.jpg", 'JPEG', quality=80)
    os.remove(background_path)

def save_logo(logo_path, app_directory):
    """
    Loads the event logo as splash image and save it into the app's resources directory
    :param logo_path: The path to the logo
    :param app_directory: The path to the app-level working directory
    :return:
    """
    destination = os.path.abspath(app_directory + '/app/src/main/res/drawable/')
    logo = Image.open(logo_path)
    logo.save(destination + "/splash_logo.png", 'PNG')
