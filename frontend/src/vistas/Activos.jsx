import React, { useEffect, useState } from 'react';
import { api } from '../api.js';

/** HU-08: el encargado registra la devolución de un préstamo activo. */
export default function Activos({ avisar }) {
  const [prestamos, setPrestamos] = useState(null);
  const cargar = () => api('GET', 'prestamos/activos').then(setPrestamos).catch(() => {});
  useEffect(() => { cargar(); }, []);

  const devolver = async (id) => {
    try {
      const p = await api('POST', `prestamos/${id}/devolver`);
      // El recurso solo queda «Disponible» si no tiene otro préstamo activo (lo decide el servidor)
      avisar(`Devolución registrada: el préstamo de «${p.recurso}» quedó «${p.estado}».`);
    } catch (x) { avisar(x.message, false); }
    cargar();
  };

  if (!prestamos) return null;
  return (
    <div className="card">
      <h2>Préstamos activos</h2>
      {prestamos.length ? (
        <table>
          <thead><tr><th>#</th><th>Recurso</th><th>Usuario</th><th>Fecha</th><th>Horario</th><th>Estado</th><th></th></tr></thead>
          <tbody>
            {prestamos.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td><td>{p.recurso}</td><td>{p.usuario}</td><td>{p.fecha}</td><td>{p.horaInicio} – {p.horaFin}</td>
                <td><span className={'tag ' + p.estado}>{p.estado}</span></td>
                <td><button className="ok" onClick={() => devolver(p.id)}>Registrar devolución</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : <div className="vacio">No hay préstamos activos.</div>}
    </div>
  );
}
