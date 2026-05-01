# Architecture

StallMart is organized by runtime:

- `app/`: WeChat Mini Program.
- `server/`: Spring Boot API.
- `docker/`: local dependencies and API image.
- `docs/`: project guide, API contracts and specifications.

Current server implementation focuses on API contract and tests. MySQL/Redis/JWT persistence is still a follow-up task.
