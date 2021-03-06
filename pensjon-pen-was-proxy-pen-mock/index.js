const express = require('express')
const app = express()
const port = 8080

app.get('/pen/api/simuler/alderspensjon/v3', (req, res) => {
  res.status(401)
      .send('<!doctype html><html lang="en">' +
          '<head>' +
          '<title>HTTP Status 401 – Unauthorized</title>' +
          '<style type="text/css">' +
          'body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3,' +
          ' b {color:white;background-color:#525D76;}' +
          ' h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;}' +
          ' p {font-size:12px;} a {color:black;}' +
          ' .line {height:1px;background-color:#525D76;border:none;}' +
          '</style>' +
          '</head>' +
          '<body>' +
          '<h1>HTTP Status 401 – Unauthorized</h1>' +
          '<hr class="line" />' +
          '<p><b>Type</b> Status Report</p>' +
          '<p><b>Message</b> Authentication Failed: No JWT token found in request headers</p>' +
          '<p><b>Description</b> The request has not been applied because it lacks valid' +
          ' authentication credentials for the target resource.</p>' +
          '<hr class="line" />' +
          '<h3>Apache Tomcat/9.0.45</h3>' +
          '</body></html>')
})

app.listen(port, () => {
  console.log(`Mock listening at http://localhost:${port}`)
})
