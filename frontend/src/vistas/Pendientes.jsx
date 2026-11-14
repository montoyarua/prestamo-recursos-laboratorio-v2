import React, { useEffect, useState } from 'react';
import { api } from '../api.js';

/** HU-07: el encargado aprueba o rechaza las solicitudes pendientes. */
export default function Pendientes({ avisar }) {
  const [solicitudes, setSolicitudes] = useState(null);
  const cargar = () => api('GET', 'solicitudes/pendientes').then(setSolicitudes).catch(() => {});
  useEffect(() => { cargar(); }, []);

  const decidir = async (id, accion) => {
    try {
      const s = await api('POST', `solicitudes/${id}/${accion}`);
      avisar(`Solicitud #${s.id} ${s.estado.toLowerCase()}. Se notificó al solicitante.`);
    } catch (x) { avisar(x.message, false); }
    cargar();
  };

  if (!solicitudes) return null;
  return (
    <div className="card">
      <h2>Solicitudes pendientes</h2>
      {solicitudes.length ? (
        <table>
          <thead><tr><th>#</th><th>Solicitante</th><th>Recurso</th><th>Fecha</th><th>Horario</th><th>Motivo</th><th>Decisión</th></tr></thead>
          <tbody>
            {solicitudes.map((s) => (
              <tr key={s.id}>
                <td>{s.id}</td><td>{s.solicitante}</td><td>{s.recurso}</td><td>{s.fecha}</td><td>{s.horaInicio} – {s.horaFin}</td><td>{s.motivo}</td>
                <td>
                  <button className="ok" onClick={() => decidir(s.id, 'aprobar')}>Aprobar</button>{' '}
                  <button className="no" onClick={() => decidir(s.id, 'rechazar')}>Rechazar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : <div className="vacio">No hay solicitudes pendientes.</div>}
    </div>
  );
}
