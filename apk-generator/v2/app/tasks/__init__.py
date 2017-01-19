from app import celery
from app.generator.generator import Generator
from app.utils.libs.request_context_task import RequestContextTask


@celery.task(base=RequestContextTask, name='generate.app', bind=True, throws=(Exception,))
def generate_app_task(self, config, payload, via_api=False, identifier=None):
    return generate_app_task_base(config=config,
                                  payload=payload,
                                  via_api=via_api,
                                  identifier=identifier,
                                  task_handle=self
                                  )


def generate_app_task_base(config, payload, via_api=False, identifier=None, task_handle=None):
    generator = Generator(config=config, via_api=via_api, identifier=identifier, task_handle=task_handle)
    generator.normalize(
        creator_email=payload.get('creator_email', None),
        endpoint_url=payload.get('endpoint_url', None),
        zip_file=payload.get('zip_file', None)
    )
    return generator.generate(should_notify=True)
