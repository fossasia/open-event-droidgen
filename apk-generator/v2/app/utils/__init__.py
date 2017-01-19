# encoding=utf8

import shutil
import zipfile
import os
from tempfile import mkstemp
import errno

import bleach
import hashlib


def hash_file(_file):
    """
    Hash a file using MD5
    :param _file:
    :return:
    """
    return hashlib.md5(str(os.path.getmtime(_file))).hexdigest()


def clear_dir(folder):
    """
    Remove all files in a directory but leave the directory itself untouched. (non-recursive)
    :param folder:
    :return:
    """
    for the_file in os.listdir(folder):
        file_path = os.path.join(folder, the_file)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
        except Exception as e:
            print(e)


def get_build_tools_version(build_gradle_path):
    """
    Get the build tools version from a gradle file
    :param build_gradle_path:
    :return:
    """
    version = None
    with open(build_gradle_path) as f:
        for line in f:
            if "buildToolsVersion" in line:
                version = line.replace('buildToolsVersion', '').replace('"', '').replace('\'', '').strip()
    return version


def replace(file_path, pattern, subst):
    """
    Replace a string in a file
    :param file_path: The path to the file
    :param pattern: The patter to search for
    :param subst: The substitute string
    :return:
    """
    # Create temp file
    fh, abs_path = mkstemp()
    with open(abs_path, 'w') as new_file:
        with open(file_path) as old_file:
            for line in old_file:
                new_file.write(line.replace(pattern, subst))
    os.close(fh)
    # Remove original file
    os.remove(file_path)
    # Move new file
    shutil.move(abs_path, file_path)


def unzip(source_file, target_dir):
    """
    Unzip a zip archive into a target directory
    :param source_file: the path to the archive
    :param target_dir: the path to the destination directory
    :return:
    """
    with open(source_file, "rb") as zip_src:
        zip_file = zipfile.ZipFile(zip_src)
        for member in zip_file.infolist():
            target_path = os.path.join(target_dir, member.filename)
            if target_path.endswith('/'):  # folder entry, create
                try:
                    os.makedirs(target_path)
                except (OSError, IOError) as err:
                    # Windows may complain if the folders already exist
                    if err.errno != errno.EEXIST:
                        raise
                continue
            with open(target_path, 'wb') as outfile, zip_file.open(member) as infile:
                shutil.copyfileobj(infile, outfile)


def strip_tags(html):
    """
    Strip all HTML Tags in a string
    :param html:
    :return:
    """
    return bleach.clean(html, tags=[], attributes={}, styles=[], strip=True)


def allowed_file(filename, allowed_extensions):
    """
    Check if a file belongs to a specific set of allowed extensions based on filename
    :param filename:
    :param allowed_extensions:
    :return:
    """
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in allowed_extensions
