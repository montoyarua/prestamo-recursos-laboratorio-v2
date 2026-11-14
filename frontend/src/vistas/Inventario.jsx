import React, { useEffect, useState } from 'react';
import { api, datosDelFormulario } from '../api.js';

/** HU-06: el encargado registra, modifica y elimina laboratorios y equipos. */
export default function Inventario({ avisar }) {
  const [recursos, setRecursos] = useState(null);
  const [editandoId, setEditandoId] = useState(null);
  const [tipo, setTipo] = useState('LABORATORIO');
  const cargar = () => api('GET', 'recursos').then(setRecursos).catch(() => {});
  useEffect(() => { cargar(); }, []);
  if (!recursos) return null;

  const e = editandoId ? recursos.find((r) => r.id === editandoId) : null;
  const tipoActual = e ? e.tipo : tipo;

  const guardar = async (ev) => {
    ev.preventDefault();
    const formulario = ev.target;
    const d = datosDelFormulario(formulario); if (e) d.tipo = e.tipo; else d.tipo = tipo;
    try {
      if (e) { await api('PUT', 'recursos/' + e.id, d); avisar('Recurso modificado'); setEditandoId(null); }
      else { const r = await api('POST', 'recursos', d); avisar(`«${r.nombre}» registrado con estado «${r.estado}»`); formulario.reset(); }
      cargar();
    } catch (x) { avisar(x.message, false); }
  };
  const eliminar = async (id) => {
    try { await api('DELETE', 'recursos/' + id); avisar('Recurso eliminado'); } catch (x) { avisar(x.message, false); }
    cargar();
  };

  return (
    <>
      <div className="card">
        <h2>{e ? 'Modificar recurso #' + e.id : 'Registrar laboratorio o equipo'}</h2>
        <form id="f" key={e ? e.id : 'nuevo'} onSubmit={guardar}>
          <label className="f">Tipo
            <select name="tipo" id="tipo" value={tipoActual} disabled={!!e} onChange={(ev) => setTipo(ev.target.value)}>
              <option value="LABORATORIO">Laboratorio</option><option value="EQUIPO">Equipo</option>
            </select>
          </label>
          <label className="f">Nombre<input name="nombre" defaultValue={e ? e.nombre : ''} /></label>
          {tipoActual === 'LABORATORIO' ? (
            <>
              <label className="f lab">Ubicación<input name="ubicacion" defaultValue={e && e.ubicacion ? e.ubicacion : ''} /></label>
              <label className="f lab">Capacidad<input name="capacidad" type="number" min="1" defaultValue={e && e.capacidad ? e.capacidad : ''} /></label>
            </>
          ) : (
            <>
              <label className="f eq">Marca<input name="marca" defaultValue={e && e.marca ? e.marca : ''} /></label>
              <label className="f eq">Modelo<input name="modelo" defaultValue={e && e.modelo ? e.modelo : ''} /></label>
              <label className="f eq">Número de serie<input name="numeroSerie" defaultValue={e && e.numeroSerie ? e.numeroSerie : ''} /></label>
            </>
          )}
          <div className="full">
            <button className="p" type="submit">{e ? 'Guardar cambios' : 'Guardar recurso'}</button>{' '}
            {e && <button className="s" type="button" id="cancel" onClick={() => setEditandoId(null)}>Cancelar</button>}
          </div>
        </form>
      </div>
      <div className="card">
        <h2>Inventario</h2>
        <table>
          <thead><tr><th>#</th><th>Tipo</th><th>Nombre</th><th>Detalle</th><th>Estado</th><th></th></tr></thead>
          <tbody>
            {recursos.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td><td>{r.tipo === 'EQUIPO' ? 'Equipo' : 'Laboratorio'}</td><td>{r.nombre}</td><td>{r.detalle}</td>
                <td><span className={'tag ' + r.estado}>{r.estado}</span></td>
                <td>
                  <button className="s" onClick={() => setEditandoId(r.id)}>Modificar</button>{' '}
                  <button className="s" onClick={() => eliminar(r.id)}>Eliminar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
