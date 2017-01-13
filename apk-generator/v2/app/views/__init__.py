from flask import Blueprint
from flask import render_template

views = Blueprint('views', __name__)


@views.route('/', methods=['GET', ])
def index():
    return render_template('index.html')


@views.route('/', methods=['POST', ])
def index_process():
    return render_template('index.html')


@views.route('/api/v1/status/<string:uuid>', methods=['GET', ])
def api_status(uuid):
    return render_template('index.html')


@views.route('/api/v1/generate', methods=['POST', ])
def api_process():
    return render_template('index.html')
