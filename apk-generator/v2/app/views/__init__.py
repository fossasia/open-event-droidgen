import os
import uuid

import datetime
import validators
from flask import Blueprint, jsonify
from flask import render_template
from flask import request, current_app as app
from werkzeug.utils import secure_filename

from app.utils import allowed_file

VALID_DATA_SOURCES = ['json_upload', 'api_endpoint']

views = Blueprint('views', __name__)


@views.route('/', methods=['GET', ])
def index():
    return render_template('index.html')


@views.route('/', methods=['POST', ])
def index_process():
    email = request.form.get('email', None)
    data_source = request.form.get('data-source', None)

    if not email or not data_source or data_source not in VALID_DATA_SOURCES or not validators.email(email):
        return jsonify(status='error', message='invalid data'), 400

    payload = {
        'creator_email': email
    }

    if data_source == 'api_endpoint':
        api_endpoint = request.form.get('api-endpoint', None)
        payload['endpoint_url'] = api_endpoint
    elif data_source == 'json_upload':
        if 'json-upload' not in request.files:
            return jsonify(status='error', message='data file is required for the selected source'), 400
        uploaded_file = request.files['json-upload']
        if uploaded_file.filename == '':
            return jsonify(status='error', message='data file is required for the selected source'), 400
        if uploaded_file and allowed_file(uploaded_file.filename, ['zip']):
            filename = secure_filename(uploaded_file.filename)
            file_save_location = os.path.join(app.config['UPLOAD_DIR'], filename)
            uploaded_file.save(file_save_location)
            payload['zip_file'] = file_save_location

    identifier = str(uuid.uuid4())

    from app.tasks import generate_app_task  # A Local import to avoid circular import
    task = generate_app_task.delay(config=app.config, payload=payload, via_api=False, identifier=identifier)
    return jsonify(status='ok', identifier=identifier, started_at=datetime.datetime.now(), task_id=task.id)
