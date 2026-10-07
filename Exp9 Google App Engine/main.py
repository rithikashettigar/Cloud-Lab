from flask import Flask, Response

app = Flask(__name__)


@app.route("/")
def index():
    return Response("Hello there Rithika", content_type="text/plain")


if __name__ == "__main__":
    # Local development server (same role as the App Engine Launcher's "Run")
    app.run(host="127.0.0.1", port=8080, debug=True)
