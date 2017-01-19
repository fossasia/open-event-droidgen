import os
import uuid

import datetime
import validators
from flask import Blueprint, jsonify
from flask import render_template
from flask import request, current_app as app
from flask import send_from_directory
from werkzeug.utils import secure_filename

from app.utils import allowed_file, hash_file

VALID_DATA_SOURCES = ['json_upload', 'api_endpoint']

views = Blueprint('views', __name__)


@views.route('/', methods=['GET', ])
def index():
    """
    Display the generator GUI
    :return:
    """
    return render_template('index.html',
                           main_css=hash_file(os.path.abspath(app.config['STATICFILES_DIR'] + '/css/main.css')),
                           main_js=hash_file(os.path.abspath(app.config['STATICFILES_DIR'] + '/js/main.js')),
                           utils_js=hash_file(os.path.abspath(app.config['STATICFILES_DIR'] + '/js/utils.js')))


@views.route('/favicon.ico')
def favicon():
    """
    Serve the favicon
    :return:
    """
    return send_from_directory(app.config['STATICFILES_DIR'], 'favicon.ico', mimetype='image/vnd.microsoft.icon')


@views.route('/health-check/')
def health_check():
    """
    Health check for Kubernetes
    :return:
    """
    return jsonify({
        "status": "ok"
    })


@views.route('/', methods=['POST', ])
def process(data=None, via_api=False):
    """
    Start the generation process when the form is submitted
    :return:
    """
    if not data:
        data = request.form
    email = data.get('email', None)
    data_source = data.get('data-source', None)

    if not email or not data_source or data_source not in VALID_DATA_SOURCES or not validators.email(email):
        return jsonify(status='error', message='invalid data'), 400

    payload = {
        'creator_email': email
    }

    identifier = str(uuid.uuid4())

    if data_source == 'api_endpoint':
        api_endpoint = data.get('api-endpoint', None)
        if not api_endpoint or not validators.url(api_endpoint):
            return jsonify(status='error', message='invalid endpoint url'), 400
        payload['endpoint_url'] = api_endpoint
    elif data_source == 'json_upload':
        if 'json-upload' not in request.files:
            return jsonify(status='error', message='data file is required for the selected source'), 400
        uploaded_file = request.files['json-upload']
        if uploaded_file.filename == '':
            return jsonify(status='error', message='data file is required for the selected source'), 400
        if uploaded_file and allowed_file(uploaded_file.filename, ['zip']):
            filename = secure_filename(identifier)
            file_save_location = os.path.join(app.config['UPLOAD_DIR'], filename)
            uploaded_file.save(file_save_location)
            payload['zip_file'] = file_save_location

    from app.tasks import generate_app_task  # A Local import to avoid circular import
    task = generate_app_task.delay(config=app.config, payload=payload, via_api=via_api, identifier=identifier)
    return jsonify(status='ok', identifier=identifier, started_at=datetime.datetime.now(), task_id=task.id)
