from marrow.mailer import Mailer, Message

from flask import current_app
from app.utils import strip_tags
import requests
from celery.utils.log import get_task_logger


# Mails APK using API/SMTP/Sendgrid depending on a configuration

logger = get_task_logger(__name__)

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

        email_service = current_app.config['EMAIL_SERVICE']

        payload = {
            'to': to,
            'from': current_app.config['FROM_EMAIL'],
            'subject': subject,
            'message': message,
            'attachment': file_attachment,
        }
        if not via_api:
            if email_service == 'smtp':
                Notification.send_mail_via_smtp_(payload)
            else:
                Notification.send_email_via_sendgrid_(payload)

    @staticmethod
    def send_mail_via_smtp_(payload):

        """
        Send email via SMTP
        :param config:
        :param payload:
        :return:
        """
        smtp_encryption = current_app.config['SMTP_ENCRYPTION']
        if smtp_encryption == 'tls':
            smtp_encryption = 'required'
        elif smtp_encryption == 'ssl':
            smtp_encryption = 'ssl'
        elif smtp_encryption == 'tls_optional':
            smtp_encryption = 'optional'
        else:
            smtp_encryption = 'none'
        config = {
            'host': current_app.config['SMTP_HOST'],
            'username': current_app.config['SMTP_USERNAME'],
            'password': current_app.config['SMTP_PASSWORD'],
            'encryption': smtp_encryption,
            'port': current_app.config['SMTP_PORT'],
        }
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
        message.plain = strip_tags(payload['message'])
        message.rich = payload['message']
        message.attach(payload['attachment'], data=None, maintype=None, subtype=None, inline=False)
        mailer.send(message)
        mailer.stop()

    @staticmethod
    def send_email_via_sendgrid_(payload):

        key = current_app.config['SENDGRID_KEY']
        if not key:
            logger.info('Sendgrid key not defined')
            return
        headers = {
            "Authorization": ("Bearer " + key)
        }
        requests.post(
            "https://api.sendgrid.com/api/mail.send.json",
            data=payload,
            headers=headers
        )
