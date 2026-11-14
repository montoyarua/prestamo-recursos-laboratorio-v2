import React, { useEffect, useState } from 'react';
import { api, datosDelFormulario } from '../api.js';

/** HU-02 y HU-03: registrar una solicitud de préstamo. */
export default function Solicitar({ avisar, prefill, limpiar }) {
  const [recursos, setRecursos] = useState(null);
  useEffect(() => { api('GET', 'recursos').then(setRecursos).catch((x) => avisar(x.message, false)); }, []);
  if (!recursos) return null;

  const enviar = async (e) => {
    e.preventDefault();
    const formulario = e.target;
    try {
      const s = await api('POST', 'solicitudes', datosDelFormulario(formulario));
      avisar(`Solicitud #${s.id} registrada con estado «${s.estado}»`);
      limpiar(); formulario.reset();
    } catch (x) { avisar(x.message, false); }
  };

  return (
    <div className="card">
      <h2>Solicitar préstamo de un recurso</h2>
      <form id="f" onSubmit={enviar}>
        <label className="f full">Recurso
          <select name="recursoId" defaultValue={prefill.recursoId ? String(prefill.recursoId) : ''}>
            <option value="">— Seleccione —</option>
            {recursos.map((r) => (
              <option key={r.id} value={String(r.id)}>{r.nombre} ({r.tipo === 'EQUIPO' ? 'equipo' : 'laboratorio'}) — {r.detalle}</option>
            ))}
          </select>
        </label>
        <label className="f">Fecha<input type="date" name="fecha" defaultValue={prefill.fecha || ''} /></label>
        <label className="f">&nbsp;</label>
        <label className="f">Hora de inicio<input type="time" name="horaInicio" defaultValue={prefill.horaInicio || ''} /></label>
        <label className="f">Hora de fin<input type="time" name="horaFin" defaultValue={prefill.horaFin || ''} /></label>
        <label className="f full">Motivo<textarea name="motivo" rows="2" placeholder="Para qué necesita el recurso" /></label>
        <div className="full"><button className="p" type="submit">Enviar solicitud</button></div>
      </form>
    </div>
  );
}
