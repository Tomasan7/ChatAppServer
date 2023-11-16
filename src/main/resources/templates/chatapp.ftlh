<html lang="en">
<head>
    <link rel="stylesheet" href="/static/chat-styles.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <script src="/static/script.js" defer></script>
    <title>Chat app</title>
</head>
<body>

<div id="messages">
    <#list messages as message>
        <p class="message"><span class="message-author">${message.author}: </span>${message.content}</p>
    </#list>
</div>


<div id="compose-bar">
    <form id="message-form" class="hide" action="/send-message" method="post"></form>
    <div id="message-bar">
        <button id="emoji-btn">
            <span class="material-icons">mood</span>
        </button>
        <input form="message-form" id="message-field" name="message" placeholder="Message">
        <!-- Workaround: https://stackoverflow.com/questions/572768/styling-an-input-type-file-button/25825731#25825731-->
        <label id="image-input-label" class="img-btn">
            <input id="image-input" class="hide" form="message-form" type="file" name="image">
            <span class="material-icons">image</span>
        </label>
    </div>
    <button id="send-btn" form="message-form" type="submit">
        <span class="material-icons">send</span>
    </button>
</div>
</body>
</html>
