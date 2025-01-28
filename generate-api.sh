# This generates the models.
# Notice the -t option, without it the custom templates will be ignored.
openapi-generator-cli generate \
  -t src/main/resources/templates \
  -g kotlin-spring \
  -i src/main/resources/openapi/example-api.json \
  -o build/generated/example-api \
  --model-package no.kantega.generated.example \
  --global-property models \
  --additional-properties useSpringBoot3=true

# This generates the same models but with mutable fields instead.
openapi-generator-cli generate \
  -t src/main/resources/templates \
  -g kotlin-spring \
  -i src/main/resources/openapi/example-api.json \
  -o build/generated/mutable-example-api \
  --model-package no.kantega.generated.example.mutable \
  --model-name-prefix Mutable \
  --global-property models \
  --additional-properties useSpringBoot3=true,modelMutable=true

