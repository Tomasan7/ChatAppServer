// Get the current hostname and protocol
const host = window.location.hostname;
const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const port = window.location.port

// Construct the WebSocket endpoint relative to the current domain
const socket = new WebSocket(`${protocol}//${host}:${port}/messages-live`);

// Event handler for when the connection is opened
socket.addEventListener('open', (event) =>
{
    console.log('WebSocket connection opened:', event);
});

// Event handler for incoming messages
socket.addEventListener('message', (event) =>
{
    // Assuming the message is a JSON string, parse it
    const message = JSON.parse(event.data);

    appendMessage(message)
});

// Event handler for when an error occurs
socket.addEventListener('error', (event) =>
{
    console.error('WebSocket error:', event);
});

// Event handler for when the connection is closed
socket.addEventListener('close', (event) =>
{
    console.log('WebSocket connection closed:', event);
});

let messagesEle = document.getElementById("messages");

function appendMessage(message)
{
    const messageBubble = document.createElement("div");
    messageBubble.classList.add("message-bubble");

    const authorEle = document.createElement("p");
    authorEle.style.color = stringToColor(message.author);
    authorEle.classList.add("message-author");
    authorEle.textContent = message.author;
    messageBubble.appendChild(authorEle);

    const textEle = document.createElement("p");
    textEle.classList.add("message-text");
    textEle.textContent = escapeHtml(message.content);
    messageBubble.appendChild(textEle);

    const timeEle = document.createElement("p");
    timeEle.classList.add("message-time");
    timeEle.textContent = message.timestamp;
    messageBubble.appendChild(timeEle);

    messagesEle.appendChild(messageBubble);
}

const messageField = document.getElementById("message-field")

messageField.addEventListener('keyup', function(event) {
    if (event.key === "Enter") {
        onSend();
    }
});

function onSend()
{
    let messageContent = messageField.value;

    if (isBlank(messageContent))
        return

    socket.send(messageContent)

    let message = {
        author: getCookie("username"),
        content: messageContent,
        timestamp: getCurrentTimestamp()
    }

    appendMessage(message)

    messageField.value = ""
}

function escapeHtml(unsafe)
{
    return unsafe.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}

function isBlank(str)
{
    return (!str || /^\s*$/.test(str));
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function getCurrentTimestamp() {
    // Create a new Date object
    var now = new Date();

    // Get hours, minutes, and seconds from the Date object
    var hours = now.getHours();
    var minutes = now.getMinutes();
    var seconds = now.getSeconds();

    // Add leading zeros if needed
    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;

    // Return the formatted time string (HH:mm:ss)
    return hours + ":" + minutes + ":" + seconds;
}


function stringToColor(inputString) {
    // Simple hash function to convert the string into a numeric value
    var hash = 0;
    for (var i = 0; i < inputString.length; i++) {
        hash = inputString.charCodeAt(i) + ((hash << 5) - hash);
    }

    // Map the numeric value to RGB components
    var r = (hash & 0xFF0000) >> 16;
    var g = (hash & 0x00FF00) >> 8;
    var b = hash & 0x0000FF;

    // Convert RGB to hexadecimal color
    var color = "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);

    return color;
}
