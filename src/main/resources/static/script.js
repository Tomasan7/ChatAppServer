const host = window.location.hostname;
const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const port = window.location.port ? window.location.port : 80;

const socket = new WebSocket(`${protocol}//${host}:${port}/messages-live`);

socket.addEventListener('message', (event) =>
{
    const wsMessage = event.data;
    const split = wsMessage.split("\n");

    switch (split[0])
    {
        case "JOIN":
            onUserJoin(split[1]);
            break;
        case "LEAVE":
            onUserLeave(split[1]);
            break;
        case "MESSAGE":
            appendMessage(JSON.parse(split[1]));
            break;
    }
});

socket.addEventListener('error', event => console.error('WebSocket error:', event))
socket.addEventListener('close', event => console.log('WebSocket connection closed:', event))

const messageFieldEle = document.getElementById("message-field")
messageFieldEle.focus()

messageFieldEle.addEventListener('keyup', event =>
{
    if (event.key === "Enter")
        onSend();
});

let messagesEle = document.getElementById("messages");
messagesEle.querySelectorAll(".message-author").forEach(authorEle => authorEle.style.color = stringToColor(authorEle.textContent));

function appendMessage(message)
{
    const messageBubble = document.createElement("div");
    messageBubble.classList.add("message-bubble");

    if (message.author === getCookie("username"))
        messageBubble.classList.add("message-bubble-self");

    const authorEle = document.createElement("p");
    authorEle.style.color = stringToColor(message.author);
    authorEle.classList.add("message-author");
    authorEle.textContent = escapeHtml(message.author);
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

function onSend()
{
    let messageContent = messageFieldEle.value;

    if (isBlank(messageContent))
        return

    socket.send(messageContent)
    messageFieldEle.value = ""
}

const onlineUsersEle = document.getElementById("online-users");

function onUserJoin(username)
{
    const userEle = document.createElement("p");
    userEle.classList.add("online-user");
    userEle.textContent = username;
    onlineUsersEle.appendChild(userEle);
}

function onUserLeave(username)
{
    onlineUsersEle.querySelectorAll(".online-user").forEach(userEle =>
    {
        if (userEle.textContent === username)
            userEle.remove();
    });
}

function escapeHtml(unsafe)
{
    return unsafe.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}

function isBlank(str)
{
    return (!str || /^\s*$/.test(str));
}

function getCookie(name)
{
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function stringToColor(inputString)
{
    // Simple hash function to convert the string into a numeric value
    let hash = 0;
    for (let i = 0; i < inputString.length; i++)
    {
        hash = inputString.charCodeAt(i) + ((hash << 5) - hash);
    }

    // Map the numeric value to RGB components
    const r = (hash & 0xFF0000) >> 16;
    const g = (hash & 0x00FF00) >> 8;
    const b = hash & 0x0000FF;

    // Convert RGB to hexadecimal color
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
}
