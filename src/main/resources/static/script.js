// Get the current hostname and protocol
const host = window.location.hostname;
const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const port = window.location.port

// Construct the WebSocket endpoint relative to the current domain
const socket = new WebSocket(`${protocol}//${host}:${port}/messages-live`);

// Event handler for when the connection is opened
socket.addEventListener('open', (event) => {
    console.log('WebSocket connection opened:', event);
});

// Event handler for incoming messages
socket.addEventListener('message', (event) => {

    const escapeHtml = (unsafe) => {
        return unsafe.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
    }

    // Assuming the message is a JSON string, parse it
    const message = JSON.parse(event.data);

    let messagesEle = document.getElementById("messages");
    let p = document.createElement("p");
    let span = document.createElement("span");
    p.className = "message"
    span.className = "message-author"
    span.textContent = escapeHtml(message.author) + ": "
    p.appendChild(span)
    p.append(escapeHtml(message.content))
    messagesEle.appendChild(p)
});

// Event handler for when an error occurs
socket.addEventListener('error', (event) => {
    console.error('WebSocket error:', event);
});

// Event handler for when the connection is closed
socket.addEventListener('close', (event) => {
    console.log('WebSocket connection closed:', event);
});
