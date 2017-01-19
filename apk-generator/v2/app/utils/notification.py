from marrow.mailer import Mailer, Message

from app.utils import strip_tags

# TODO Notifications have to be implemented. API/SMTP/Sendgrid depending on a configuration


class Notification:

    def __init__(self):
        pass

    @staticmethod
    def send(to, subject, message, file_attachment, via_api):
        """
        Send notification using the appropriate mode
        :param to:
        :param subject:
        :param message:
        :param file_attachment:
        :param via_api:
        :return:
        """
        pass

    @staticmethod
    def send_mail_via_smtp_(config, payload):
        """
        Send email via SMTP
        :param config:
        :param payload:
        :return:
        """
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
