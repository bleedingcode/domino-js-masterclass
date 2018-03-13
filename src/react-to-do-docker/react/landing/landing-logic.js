import IOClient from 'socket.io-client';
import Globals from '../globals';
import { processSignInResult } from '../core/core-actions';

export const connectWebSocket = () => {
  Globals.ws = IOClient.connect(Globals.wsUrl, {reconnect: true});

  Globals.ws.on('connect', function (socket) {
    console.log("Main Web Socket Connected!");
  });

  Globals.ws.on('init-user-session', function (id) {
    Globals.user.socketId = id;
  });

  Globals.ws.on('to-do-app-response', processSignInResult);

  return null;
}

export const disconnectWebSocket = () => {
  console.log("Main Web Socket Disconnected");
  Globals.ws.disconnect();
  Globals.ws = null;
  return true;
}