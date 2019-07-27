#!/bin/sh

curl -v -H "Content-Type: application/json" -H "Accept: application/json" https://vagivagi-blog-api.azurewebsites.net/webhook -d "$(cat src/test/resources/sample-create-request.json)" -H "X-Hub-Signature: $(cat src/test/resources/sample-create-request.txt)"
