from app import celery
from app.generator.generator import Generator
from app.utils.libs.request_context_task import RequestContextTask


@celery.task(base=RequestContextTask, name='generate.app', bind=True)
def generate_app_task(self, config, payload, via_api=False, identifier=None, build_type=None, theme_colors=None):
    """
    The celery task that starts the generator
    :param self:
    :param config:
    :param payload:
    :param via_api:
    :param identifier:
    :return:
    """
    return generate_app_task_base(config=config,
                                  payload=payload,
                                  via_api=via_api,
                                  identifier=identifier,
                                  task_handle=self,
                                  build_type=build_type,
                                  theme_colors=theme_colors)


def generate_app_task_base(config, payload, via_api=False, identifier=None, task_handle=None, build_type=None, theme_colors=None):
    """
    The base task that starts the generator by calling correct methods. Can be called even without celery.
    :param config:
    :param payload:
    :param via_api:
    :param identifier:
    :param task_handle:
    :return:
    """
    generator = Generator(config=config, via_api=via_api, identifier=identifier, task_handle=task_handle, build_type=build_type, theme_colors=theme_colors)
    generator.normalize(
        creator_email=payload.get('creator_email', None),
        is_auth_enabled=payload.get('is_auth_enabled', False),
        endpoint_url=payload.get('endpoint_url', None),
        zip_file=payload.get('zip_file', None)
    )
    return generator.generate(should_notify=True)
