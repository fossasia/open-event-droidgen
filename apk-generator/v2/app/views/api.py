from celery.result import AsyncResult
from flask import Blueprint, jsonify, current_app

api = Blueprint('api', __name__, url_prefix='/api/v2')

TASK_RESULTS = {}


@api.route('/app/<string:task_id>/status', methods=['GET', ])
def app_status(task_id):
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
    return jsonify(identifier=identifier, status='processing')


@api.route('/generate', methods=['POST', ])
def app_generate():
    return jsonify(status='processing')
