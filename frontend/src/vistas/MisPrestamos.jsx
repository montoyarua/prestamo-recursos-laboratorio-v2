import React, { useEffect, useState } from 'react';
import { api } from '../api.js';

/** HU-05: mis préstamos activos y finalizados. */
export default function MisPrestamos() {
  const [prestamos, setPrestamos] = useState(null);
  useEffect(() => { api('GET', 'prestamos/mios').then(setPrestamos).catch(() => {}); }, []);
  if (!prestamos) return null;
  return (
    <div className="card">
      <h2>Mis préstamos</h2>
      {prestamos.length ? (
        <table>
          <thead><tr><th>#</th><th>Recurso</th><th>Fecha</th><th>Horario</th><th>Estado</th><th>Devuelto</th></tr></thead>
          <tbody>
            {prestamos.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td><td>{p.recurso}</td><td>{p.fecha}</td><td>{p.horaInicio} – {p.horaFin}</td>
                <td><span className={'tag ' + p.estado}>{p.estado}</span></td>
                <td>{p.fechaDevolucion ? p.fechaDevolucion.replace('T', ' ').slice(0, 16) : '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : <div className="vacio" id="sinPrestamos">No tiene préstamos registrados.</div>}
    </div>
  );
}
