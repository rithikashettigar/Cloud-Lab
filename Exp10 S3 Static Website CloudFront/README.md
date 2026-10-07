# Experiment 10 - Static website on S3 with CloudFront and signed URLs

## Aim
To deploy a static web application using Amazon S3 and secure it with signed URLs.

## Procedure
1. In S3, create the bucket `rithika-static-website-cclab-2026` in us-east-1 and uncheck **Block all public access**.
2. Properties > **Static website hosting** > Enable with `index.html` and `error.html`.
3. Permissions > **Bucket policy**: allow public `s3:GetObject` on the website files only (the manual's `s3:*` would let anyone delete the files).
4. Upload `index.html`, `styles.css`, `error.html` and a private file `private/lab-result.html`.
5. Open the S3 website endpoint to see the site and the custom 404 page.
6. CloudFront > **Create distribution** with the S3 website endpoint (without `http://`) as origin, **Redirect HTTP to HTTPS**, GET/HEAD; set the origin protocol to HTTP only.
7. Open the CloudFront domain over HTTPS.
8. Signed URL: the private object returns 403 when opened directly; **Object actions > Share with a presigned URL** (60 minutes) produces a signed URL that opens it.

## Source code

**`website/index.html`**

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Rithika | Static Website on Amazon S3</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <header>
    <h1>Cloud Computing Lab</h1>
    <p class="sub">Experiment 10 &mdash; Static web application hosted on Amazon S3</p>
  </header>

  <main>
    <section class="card">
      <h2>Hello from Amazon S3!</h2>
      <p>This page is a static website served directly from an S3 bucket with
         <strong>static website hosting</strong> enabled, and delivered worldwide through
         <strong>Amazon CloudFront</strong>.</p>
    </section>

    <section class="grid">
      <div class="tile"><span>1</span><h3>S3 Bucket</h3><p>Stores index.html, styles.css and error.html.</p></div>
      <div class="tile"><span>2</span><h3>Bucket Policy</h3><p>Grants public read on the website files only.</p></div>
      <div class="tile"><span>3</span><h3>CloudFront</h3><p>CDN in front of S3, HTTP redirected to HTTPS.</p></div>
      <div class="tile"><span>4</span><h3>Signed URL</h3><p>Private files are reachable only through a time-limited signed URL.</p></div>
    </section>
  </main>

  <footer>Submitted by Rithika &middot; Cloud Computing Lab (2022 Scheme)</footer>
</body>
</html>
```

**`website/styles.css`**

```css
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: "Segoe UI", Arial, sans-serif; background: #f4f6fb; color: #1d2433; min-height: 100vh; display: flex; flex-direction: column; }
header { background: linear-gradient(120deg, #232f3e, #37475a); color: #fff; padding: 48px 24px; text-align: center; }
header h1 { font-size: 2.4rem; letter-spacing: .5px; }
header .sub { margin-top: 8px; color: #ff9900; font-size: 1.1rem; }
main { flex: 1; max-width: 960px; margin: 32px auto; padding: 0 16px; width: 100%; }
.card { background: #fff; border-left: 6px solid #ff9900; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.card h2 { margin-bottom: 8px; }
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-top: 24px; }
.tile { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.tile span { display: inline-block; width: 32px; height: 32px; line-height: 32px; text-align: center; border-radius: 50%; background: #232f3e; color: #ff9900; font-weight: 700; }
.tile h3 { margin: 10px 0 6px; }
footer { text-align: center; padding: 18px; background: #232f3e; color: #c9d1d9; font-size: .9rem; }
```

**`website/error.html`**

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>404 | Page not found</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <header>
    <h1>404 &mdash; Page not found</h1>
    <p class="sub">This error page is served by S3 static website hosting.</p>
  </header>
  <main><section class="card"><p><a href="index.html">Back to home</a></p></section></main>
</body>
</html>
```

**`private/lab-result.html`**

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Private content | Signed URL</title>
  <style>
    body { font-family: "Segoe UI", Arial, sans-serif; background: #0f1b2d; color: #fff; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
    .box { background: #18263d; border: 2px solid #2ecc71; border-radius: 10px; padding: 40px 48px; max-width: 640px; text-align: center; }
    h1 { color: #2ecc71; margin-top: 0; }
    code { color: #ff9900; }
  </style>
</head>
<body>
  <div class="box">
    <h1>&#128274; Private object accessed via Signed URL</h1>
    <p>This file is stored under <code>private/</code> in the S3 bucket and is <strong>not</strong> publicly readable.</p>
    <p>Opening it directly returns <code>403 Access Denied</code>. It can only be viewed through a
       time-limited <strong>presigned (signed) URL</strong> generated by the bucket owner.</p>
  </div>
</body>
</html>
```

## Result
The static website was served from S3 and through CloudFront over HTTPS, and the private object was accessible only through a time-limited signed URL.
