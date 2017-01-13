from app import celery
from app.generator.generator import Generator


@celery.task(name='generate.app')
def generate_app_task(config, payload, via_api=False):
    generator = Generator(config, via_api)
    generator.normalize(
        creator_email=payload.get('endpoint_url', None),
        endpoint_url=payload.get('endpoint_url', None),
        zip_file=payload.get('zip_file', None)
    )
    generator.generate(should_notify=True)
