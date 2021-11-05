const express = require('express')
const app = express()
const port = 8080

app.get('/pen/api/ping', (req, res) => {
  res.send('Pen Api Pong!')
})

app.listen(port, () => {
  console.log(`Mock listening at http://localhost:${port}`)
})
