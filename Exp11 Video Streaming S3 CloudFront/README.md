# Experiment 11 - Video streaming service using S3 and CloudFront

## Aim
To create a video streaming service using a private Amazon S3 bucket delivered through Amazon CloudFront.

## Procedure
1. Create the S3 bucket `rithika-video-streaming-2026`, keep **Block all public access** ON, enable versioning and SSE-S3 encryption.
2. Upload a test `.mp4` video.
3. Create a CloudFront distribution with the bucket as origin and **Origin access control** (private bucket access).
4. Viewer protocol policy **Redirect HTTP to HTTPS**, allowed methods GET/HEAD, cache policy CachingOptimized, WAF not enabled.
5. CloudFront updates the bucket policy (`AllowCloudFrontServicePrincipal`, `s3:GetObject`).
6. Direct S3 access to the video is denied (403).
7. Open `https://<distribution>.cloudfront.net/lab-video.mp4` - the video streams in the browser.

## Result
The video stored in a private S3 bucket was streamed securely through CloudFront over HTTPS.
