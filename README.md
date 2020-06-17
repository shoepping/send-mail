#### Send Mail
* Usage
    ```
    docker run -it shoepping/send-mail:20.06.17 \
        "from_address" \
        "recipient" \
        "subject" \
        "message" \
        "sendgrid_api_key"
    for multiple recipients use comma separated list
    ```
