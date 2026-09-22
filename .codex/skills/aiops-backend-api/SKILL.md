---
name: aiops-backend-api
description: Design or change REST endpoints and service behavior in this AIOps backend. Use for request and response contracts, validation, errors, pagination, and API tests.
---

# Backend API work

Trace the affected controller, request and response types, service, repository,
and `UserControllerTests` before changing an endpoint. The existing users API in
`README.md` is the current public contract; preserve its method, path, status,
and payload behavior unless the task intentionally changes them.

Put HTTP mapping and status choices in controllers or exception handlers, and
business operations and transactions in services. Cover a successful request
and the important invalid or missing resource path in API tests. For new AIOps
endpoints, establish the actual domain contract first; the repository does not
yet define incident, alert, or telemetry APIs.
