import React, { useState } from 'react';
import { api, datosDelFormulario, hoy } from '../api.js';

const tipoTexto = (t) => (t === 'EQUIPO' ? 'Equipo' : 'Laboratorio');

/** HU-01: consultar disponibilidad por fecha y horario. */
export default function Disponibilidad({ avisar, prefill, setPrefill, irASolicitar }) {
  const [resultado, setResultado] = useState(null);

  const consultar = async (e) => {
    e.preventDefault();
    const d = datosDelFormulario(e.target); setPrefill((p) => ({ ...p, ...d }));
    try {
      const recursos = await api('GET', 'recursos/disponibles?' + new URLSearchParams(d));
      setResultado({ consulta: d, recursos });
    } catch (x) { avisar(x.message, false); setResultado(null); }
  };

  return (
    <>
      <div className="card">
        <h2>Consultar disponibilidad</h2>
        <form id="f" onSubmit={consultar}>
          <label className="f">Fecha<input type="date" name="fecha" defaultValue={prefill.fecha || hoy()} /></label>
          <label className="f">&nbsp;</label>
          <label className="f">Hora de inicio<input type="time" name="horaInicio" defaultValue={prefill.horaInicio || '08:00'} /></label>
          <label className="f">Hora de fin<input type="time" name="horaFin" defaultValue={prefill.horaFin || '10:00'} /></label>
          <div className="full"><button className="p" type="submit">Ver recursos disponibles</button></div>
        </form>
      </div>
      <div id="res">
        {resultado && (
          <div className="card">
            <h2>Recursos disponibles el {resultado.consulta.fecha} de {resultado.consulta.horaInicio} a {resultado.consulta.horaFin}</h2>
            {resultado.recursos.length ? (
              <table>
                <thead><tr><th>Tipo</th><th>Nombre</th><th>Detalle</th><th></th></tr></thead>
                <tbody>
                  {resultado.recursos.map((r) => (
                    <tr key={r.id}>
                      <td>{tipoTexto(r.tipo)}</td><td>{r.nombre}</td><td>{r.detalle}</td>
                      <td><button className="s" onClick={() => irASolicitar(r.id)}>Solicitar</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : <div className="vacio" id="sinRecursos">No hay recursos disponibles en ese horario.</div>}
          </div>
        )}
      </div>
    </>
  );
}
