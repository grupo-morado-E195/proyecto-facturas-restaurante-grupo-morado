import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WS_URL = import.meta.env.VITE_WS_URL || "http://127.0.0.1:5050/ws";

/**
 * Hook reutilizable para suscribirse a un topic STOMP sobre SockJS.
 *
 * @param {string}   topic      Topic a suscribirse, ej: "/topic/mesas"
 * @param {Function} onMessage  Callback que recibe el frame de mensaje
 * @param {boolean}  [active]   Si es false, no se conecta (útil para condicionar)
 */
export function useWebSocket(topic, onMessage, active = true) {
  const clientRef = useRef(null);

  useEffect(() => {
    if (!active || !topic) return;

    const token = localStorage.getItem("token");

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(topic, onMessage);
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [topic, active]);
}
