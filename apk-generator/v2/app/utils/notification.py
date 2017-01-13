from marrow.mailer import Mailer, Message

from app.utils import strip_tags


class Notification:

    def __init__(self):
        pass

    @staticmethod
    def send(to, subject, message, file_attachment, via_api):
        pass



    @staticmethod
    def send_mail_via_smtp_task(config, payload):
        mailer_config = {
            'transport': {
                'use': 'smtp',
                'host': config['host'],
                'username': config['username'],
                'password': config['password'],
                'tls': config['encryption'],
                'port': config['port']
            }
        }

        mailer = Mailer(mailer_config)
        mailer.start()
        message = Message(author=payload['from'], to=payload['to'])
        message.subject = payload['subject']
        message.plain = strip_tags(payload['html'])
        message.rich = payload['html']
        mailer.send(message)
        mailer.stop()
