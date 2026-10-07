# Experiment 9 - Google App Engine web application

## Aim
To create and launch a web application for Google App Engine and run it on the local development server.

## Procedure
1. Create the `apps/ae-01-trivial` folder.
2. Create `app.yaml` (runtime `python312`, all URLs handled by the app) and `main.py`.
3. Run the application locally (the Google App Engine Launcher has been retired, so the app is started with `python main.py`, which serves it on port 8080 like the launcher did).
4. Open http://localhost:8080 - the page shows `Hello there Chuck`.
5. Edit `main.py` to change the name and refresh the browser - `Hello there Rithika`.
6. Watch the log: every refresh shows a `GET /` request.
7. Dealing with errors: a syntax error in `main.py` stops the server and the log shows the error; fixing it and restarting brings the app back.

## Source code

**`apps/ae-01-trivial/app.yaml`**

```yaml
runtime: python312

handlers:
- url: /.*
  script: auto
```

**`apps/ae-01-trivial/main.py`**

```python
from flask import Flask, Response

app = Flask(__name__)


@app.route("/")
def index():
    return Response("Hello there Rithika", content_type="text/plain")


if __name__ == "__main__":
    # Local development server (same role as the App Engine Launcher's "Run")
    app.run(host="127.0.0.1", port=8080, debug=True)
```

## Result
The App Engine application was created and launched on the local development server; edits and errors were observed through the browser and the log.
