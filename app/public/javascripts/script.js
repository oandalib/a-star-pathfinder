makeRows(20, 20);

function makeRows(rows, cols) {
  const container = document.getElementById('container');

  var startPosition = -1;
  var endPosition = -1;
  var barrierSet = new Set();
  var started = false;

  const reset = document.getElementById('reset');
  reset.addEventListener('click', function() {
    location.reload();
  });

  const form = document.getElementById('my-form');
  form.addEventListener('submit', function(event) {
    event.preventDefault();
    if ( endPosition == -1 || startPosition == -1) {
      window.alert("Please choose start and end positions!");
    } else {
      document.getElementById('submit').disabled = true;
      started = true;
      var barrierArray = Array.from(barrierSet);

      var data = JSON.stringify({ startPosition: startPosition,
                                  endPosition: endPosition,
                                  barrierSet: barrierArray });

      sendData(data);
    }
  });

  container.style.setProperty('--grid-rows', rows);
  container.style.setProperty('--grid-cols', cols);

  for (c = 0; c < (rows * cols); c++) {
    let cell = document.createElement("div");
    cell.setAttribute("id", c);
    cell.setAttribute("style", 'background-color: white;');
    container.appendChild(cell).className = "grid-item";
  };

  var mouseIsDown = false;
  container.addEventListener('mousedown', function (){mouseIsDown = true});
  container.addEventListener('mouseup', function (){mouseIsDown = false});
  container.addEventListener('mousemove', function(e) {
    var td = e.target;
    if (td.className !== 'grid-item') {
      return;
    }

    if (!started) {
      if (startPosition === -1) {
        if (mouseIsDown &&
            e.buttons === 1 &&
            td.id != endPosition) {

          td.style.backgroundColor = 'orange';
          startPosition = td.id;
        }
      }

      if (endPosition === -1) {
        if (mouseIsDown &&
            e.buttons === 1 &&
            td.id != startPosition) {

          td.style.backgroundColor = 'blue';
          endPosition = td.id;
        }
      }

      if (mouseIsDown) {
        if (e.buttons === 1) {
          if (td.style.backgroundColor !== 'black' &&
              td.id !== startPosition &&
              td.id !== endPosition) {

            td.style.backgroundColor = 'black';
            barrierSet.add(td.id);
          }
        } else if (e.buttons === 4) {
          if (td.style.backgroundColor !== 'white') {
            if (td.id === startPosition) {
              startPosition = -1;
            } else if (td.id === endPosition) {
              endPosition = -1;
            }
            barrierSet.delete(td.id);
            td.style.backgroundColor = 'white';
          }
        }
      }
    }
  });
};

function sendData(data) {
  fetch('/send', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: data
  });
  fetchMostRecentData();

}

function fetchMostRecentData() {
  fetch('/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  }).then(res => res.body.getReader())
    .then(reader => {
      pump();
      function pump() {
        reader.read().then(({ done, value }) => {

          if (done) {
              return;
          }

          var string = new TextDecoder("utf-8").decode(value);
          if (string === 'end') {
            return;
          }
          updateView(string);

          pump();
        });
      }
    });
}

function updateView(data) {
  var dataColor = data.charAt(0);
  var dataCellNum = data.slice(1);

  switch(dataColor) {
    case 'g':
      dataColor = 'green';
      break;
    case 'r':
      dataColor = 'red';
      break;
    case 'p':
      dataColor = 'purple';
      break;
    default:
      return;
  }

  let cell = document.getElementById(dataCellNum);
  cell.style.backgroundColor = dataColor;
}
