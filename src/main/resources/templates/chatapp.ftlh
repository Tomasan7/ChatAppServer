<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/static/chat-styles.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <script src="/static/script.js" defer></script>
    <title>Chat app</title>
</head>
<body>

<header>
    <h1>Chat app</h1>
    <h1 id="username" title="Click to logout">${username}</h1>
    <div id="online-users">
        <#list onlineUsers as user>
            <p class="online-user">${user}</p>
        </#list>
    </div>
</header>

<div id="messages">
    <#list messages as message>
        <div class="message-bubble${(message.author == username)?string(" message-bubble-self", "")}">
            <p class="message-author">${message.author}</p>
            <p class="message-text">${message.content}</p>
            <p class="message-time">${message.timestamp}</p>
        </div>
    </#list>
</div>


<div id="compose-bar">
    <div id="message-bar">
        <button id="emoji-btn">
            <span class="material-icons">mood</span>
        </button>
        <input id="message-field" name="message" placeholder="Message" autocomplete="off">
        <!-- Workaround: https://stackoverflow.com/questions/572768/styling-an-input-type-file-button/25825731#25825731-->
        <label id="image-input-label" class="img-btn">
            <!-- Not supported for now -->
            <input id="image-input" class="hide" type="file" name="image">
            <span class="material-icons">image</span>
        </label>
    </div>
    <button id="send-btn" type="submit" onclick="onSend()">
        <span class="material-icons">send</span>
    </button>
</div>
</body>
</html>
