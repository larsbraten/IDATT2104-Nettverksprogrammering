const net = require('net');
const fs = require('fs');
const sha1 = require('crypto-js/sha1')
const base64encode = require('crypto-js/enc-base64');

const SERVER_PORT = 3030;
const WSSERVER_PORT = 3031;

/* SIMPLE WEB SERVER */

const server = net.createServer((connection) => {
    connection.on('data', () => {
        console.log('Incoming request');

        fs.readFile('index.html', 'utf8', (err, data) => {
            if (err) {
                return console.log(err);
            }
            let content = data;
            content = content.replace("[@WSPORT]", WSSERVER_PORT.toString());

            connection.write(
                'HTTP/1.1 200 OK\r\nContent-Length: ' + content.length + '\r\n\r\n'
                + content);
            console.log('Served page to client');
        });
    });
}).listen(SERVER_PORT, () => {
    console.log('Listening on port %s', SERVER_PORT);
});


/* WEBSOCKET SERVER */
const getHeaderLine = (data, filter) => {
    return data
        .toString()
        .split("\r\n")
        .filter(line => line.startsWith(filter + ":"))
        .pop()
        .split(": ")
        [1];
}

const getAcceptKey = (key) => {
    return base64encode.stringify((sha1(key.concat('258EAFA5-E914-47DA-95CA-C5AB0DC85B11'))));
}

const getHandshakeResponse = (acceptKey) => {
    return "HTTP/1.1 101 Switching Protocols\r\n" +
        "Upgrade: websocket\r\n" +
        "Connection: Upgrade\r\n" +
        "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n"
}

const decryptClientMessage = (encrypted) => {
    let bytes = Buffer.from(encrypted);

    let length = bytes[1] & 127;
    let maskStart = 2;
    let dataStart = maskStart + 4;

    let string = "";

    for (let i = dataStart; i < dataStart + length; i++) {
        let byte = bytes[i] ^ bytes[maskStart + ((i - dataStart) % 4)];
        string += String.fromCharCode(byte);
    }

    return string;
}

const convertToWebSocketMessage = (message) => {
    const b = Buffer.alloc(message.length + 2);
    b.writeUInt8(129, 0);
    b.writeUInt8(message.length, 1);
    b.write(message, 2);

    return b;
}

const removeConnection = (connection) => {

    connections.forEach((conn, i) => {
        if (conn == connection) {
            connections.splice(i, 1);
            console.log('Removed connection. Connections left: ' + connections.length);
        }
    });
}

const sendToAllConnections = (message) => {
    const convertedMessage = convertToWebSocketMessage(message);

    connections.forEach((conn) => {
        conn.write(convertedMessage);
    });
}

const connections = [];

const wsServer = net.createServer((connection) => {
    console.log('New client');
    connection.upgraded = false;

    connections.push(connection);
    console.log('Connections: ' + connections.length);

    connection.on('data', (data) => {
        if (!connection.upgraded) {
            let key = getHeaderLine(data, "Sec-WebSocket-Key");
            connection.write(getHandshakeResponse(getAcceptKey(key)));
            connection.upgraded = true;

            console.log('Upgraded client connection');
        } else {
            console.log(data);
            console.log(connection.remoteAddress);

            if (data.readUInt8(0) === 136) {
                connection.end();
            }
            else {
                sendToAllConnections(decryptClientMessage(data));
            }
        }
    });

    connection.on('end', () => {
        removeConnection(connection);
        console.log('Client disconnected');
    });

    connection.on('error', (err) => {
        console.error('Error: ' + err);
    });
}).listen(WSSERVER_PORT, () => {
    console.log('WebSocket server listening on port %s', WSSERVER_PORT);
})

