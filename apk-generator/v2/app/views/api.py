import os

import validators
from celery.result import AsyncResult
from flask import Blueprint, jsonify, current_app, abort
from flask import request
from werkzeug.utils import secure_filename

from app.views import process

api = Blueprint('api', __name__, url_prefix='/api/v2')

TASK_RESULTS = {}


@api.route('/app/<string:task_id>/status', methods=['GET', ])
def app_status(task_id):
    """
    Get the status of a celery task based on task ID
    :param task_id:
    :return:
    """
    # in case of always eager, get results. don't call AsyncResult
    # which rather looks in redis
    if current_app.config.get('CELERY_ALWAYS_EAGER'):
        state = TASK_RESULTS[task_id]['state']
        info = TASK_RESULTS[task_id]['result']
    else:
        from app import celery
        result = AsyncResult(id=task_id, app=celery)
        state = result.state
        info = result.info
    # check
    if state == 'SUCCESS':
        if type(info) == dict:
            # check if is error
            if '__error' in info:
                return info['result'], info['result']['code']
        # return normal
        return jsonify(state='SUCCESS', result=info)
    elif state == 'FAILURE':
        return jsonify(state=state)
    else:
        return jsonify(state=state)


@api.route('/app/<string:identifier>/download', methods=['GET', ])
def app_download(identifier):
    """
    Download the built release apk of an application using it's identifier
    :param identifier:
    :return:
    """
    identifier = secure_filename(identifier)
    file_path = os.path.abspath(current_app.config['STATICFILES_DIR'] + '/releases/%s.apk' % identifier)
    if not os.path.isfile(file_path):
        abort(404)
    return api.send_static_file(file_path)


@api.route('/generate', methods=['POST', ])
def app_generate():
    """
    Start the generator via API
    :return:
    """
    json_input = request.get_json(force=True, silent=True)
    if not json_input:
        return jsonify(status='error', message='invalid data'), 400

    if 'email' not in json_input or not validators.email(str(json_input.get('email'))):
        return jsonify(status='error', message='Valid Email is required'), 400

    if 'endpoint' not in json_input or not validators.url(str(json_input.get('endpoint'))):
        return jsonify(status='error', message='Valid endpoint URL is required'), 400

    data = {
        'email': json_input.get('email'),
        'data-source': 'api_endpoint',
        'api-endpoint': json_input.get('endpoint')
    }

    return process(data=data, via_api=True)
