var express = require('express');
var router = express.Router();
var amqp = require('amqplib/callback_api');
var bodyParser = require('body-parser');
const { exec } = require('child_process');

router.use(bodyParser.json());

var queueCounter = 0;

router.post('/update', function(req, res, next) {
  console.log("POST - UPDATE");
  amqp.connect('amqp://guest:guest@rabbitmq:5672', function(error0, connection) {
    if (error0) {
      throw error0;
    }

    connection.createChannel(function(error1, channel) {
      if (error1) {
        throw error1;
      }

      var queue = 'queue' + queueCounter;
      console.log(queue);
      channel.assertQueue(queue, {
        durable: false
      });

      // for debugging
      // console.log(" [*] Waiting for messages in %s. To exit press CTRL+C", queue);
      channel.consume(queue, function(msg) {
        // console.log(" [x] Received %s", msg.content.toString());
        if (msg.content.toString() === 'end') {
          channel.close();
          connection.close();
          return;
        }
        res.write(msg.content.toString());
      }, {
          noAck: true
      });

    });
  });
});

router.post('/send', function(req, res, next) {
  console.log("POST - SEND");

  var command = 'cd /app/java/target/classes && ' +
      'java -cp slf4j-api-1.7.26.jar:slf4j-simple-1.7.26.jar:amqp-client-5.7.1.jar:. AStarSearch '
      + req.body.startPosition + ' ' + req.body.endPosition + ' ' + ++queueCounter + ' ' + req.body.barrierSet;

  console.log(command);
  exec(command);
});

module.exports = router;
