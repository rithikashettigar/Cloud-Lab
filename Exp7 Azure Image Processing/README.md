# Experiment 7 - Thread based image processing application in Microsoft Azure

## Aim
To implement a thread based image processing application using Microsoft Azure Blob Storage.

## Procedure
1. Sign in to the Azure portal and open **Storage accounts > Create**.
2. Create a new resource group and the storage account `rithikaimagestorage26` (Standard, LRS, India South Central - an allowed region for Azure for Students).
3. Open the storage account > **Containers > + Container**, name it `images` (private).
4. Upload multiple sample images into the container.
5. Copy the connection string from **Access keys** into a local `.env` file (never committed).
6. Install the libraries: `pip install azure-storage-blob pillow`.
7. Run `python app.py` - one thread per image downloads it, resizes it to 200x200 and uploads `processed_<name>`.
8. Verify the `processed_*` images in the container.

## Source code

**`app.py`**

```python
import io
import os
import threading
import time

from azure.storage.blob import BlobServiceClient
from PIL import Image


def load_env(path=".env"):
    """Read KEY=VALUE lines from a local .env file into os.environ."""
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    os.environ.setdefault(key.strip(), value.strip().strip('"'))


load_env()

# Azure connection (the connection string is kept in .env, never in the code)
connection_string = os.environ["AZURE_STORAGE_CONNECTION_STRING"]
container_name = "images"
blob_service_client = BlobServiceClient.from_connection_string(connection_string)


def process_image(blob_name):
    thread = threading.current_thread().name
    blob_client = blob_service_client.get_blob_client(container=container_name, blob=blob_name)

    # Download image
    data = blob_client.download_blob().readall()
    stream = io.BytesIO(data)

    # Open and process image
    img = Image.open(stream)
    original_size = img.size
    img = img.resize((200, 200))  # Resize

    # Save processed image
    output = io.BytesIO()
    img.convert("RGB").save(output, format="JPEG")
    output.seek(0)

    # Upload processed image
    new_name = "processed_" + blob_name
    blob_service_client.get_blob_client(container=container_name, blob=new_name).upload_blob(output, overwrite=True)
    print(f"[{thread}] {blob_name} {original_size} -> {new_name} (200, 200)")


def main():
    container_client = blob_service_client.get_container_client(container_name)
    blobs = [b.name for b in container_client.list_blobs() if not b.name.startswith("processed_")]
    print(f"Found {len(blobs)} images in container '{container_name}': {blobs}")

    start = time.time()
    threads = []

    # Create threads
    for blob_name in blobs:
        t = threading.Thread(target=process_image, args=(blob_name,), name=f"Thread-{len(threads) + 1}")
        threads.append(t)
        t.start()

    # Wait for completion
    for t in threads:
        t.join()

    print(f"All images processed successfully using {len(threads)} threads in {time.time() - start:.2f} s")


if __name__ == "__main__":
    main()
```

## Result
All images were processed concurrently by multiple threads and the resized `processed_*.jpg` files appeared in the Azure Blob container.
