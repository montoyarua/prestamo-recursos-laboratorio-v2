import React, { useEffect, useState } from 'react';
import { api } from '../api.js';

/** HU-04: estado de mis solicitudes y notificaciones. */
export default function MisSolicitudes() {
  const [datos, setDatos] = useState(null);
  useEffect(() => {
    Promise.all([api('GET', 'solicitudes/mias'), api('GET', 'notificaciones')]).then(([solicitudes, notificaciones]) => {
      setDatos({ solicitudes, notificaciones });
      if (notificaciones.some((n) => !n.leida)) api('POST', 'notificaciones/leidas').catch(() => {});
    }).catch(() => {});
  }, []);
  if (!datos) return null;
  const { solicitudes, notificaciones } = datos;
  return (
    <>
      <div className="card">
        <h2>Notificaciones</h2>
        {notificaciones.length
          ? notificaciones.map((n) => <div key={n.id} className={'notif ' + (n.leida ? 'leida' : '')}>{n.mensaje}</div>)
          : <div className="vacio">Sin notificaciones.</div>}
      </div>
      <div className="card">
        <h2>Mis solicitudes</h2>
        {solicitudes.length ? (
          <table>
            <thead><tr><th>#</th><th>Recurso</th><th>Fecha</th><th>Horario</th><th>Motivo</th><th>Estado</th></tr></thead>
            <tbody>
              {solicitudes.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td><td>{s.recurso}</td><td>{s.fecha}</td><td>{s.horaInicio} – {s.horaFin}</td><td>{s.motivo}</td>
                  <td><span className={'tag ' + s.estado}>{s.estado}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : <div className="vacio">Aún no ha enviado solicitudes.</div>}
      </div>
    </>
  );
}
