# Minecraft LED Strip Controller

This plugin fires a `POST` request off to an API when a player stands on a Wool block.

## Usage

Use the `/startlisten` command to enable the move event listener.
Use the `/stoplisten` to disable the event lister.

## Configuration

On start-up, the plugin will create a config dir called `LEDStripController`. A file called `application.conf` will be generated in that dir. The generated conf file will contain a place-holder value for the `app.api.endpoint` key. Change this as needed then restart the server.

Optional, set the `API_ENPOINT` environement variable.

```properties
app.api.endpoint=${?API_ENDPOINT}
```

## Operation

Plugin sends a `POST` request to the configured endpoint with the following JSON body:

```json
{
  "color_name": "<wool color>"
}
```
