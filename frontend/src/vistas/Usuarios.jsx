import React, { useEffect, useState } from 'react';
import { api, datosDelFormulario } from '../api.js';

/** HU-10: el encargado crea cuentas con rol. */
export default function Usuarios({ avisar }) {
  const [usuarios, setUsuarios] = useState(null);
  const cargar = () => api('GET', 'usuarios').then(setUsuarios).catch(() => {});
  useEffect(() => { cargar(); }, []);
  if (!usuarios) return null;

  const crear = async (ev) => {
    ev.preventDefault();
    const formulario = ev.target;
    try {
      const u = await api('POST', 'usuarios', datosDelFormulario(formulario));
      avisar(`Cuenta de ${u.nombre} creada con rol ${u.rol.toLowerCase()}`);
      formulario.reset(); cargar();
    } catch (x) { avisar(x.message, false); }
  };

  return (
    <>
      <div className="card">
        <h2>Registrar usuario</h2>
        <form id="f" onSubmit={crear}>
          <label className="f">Nombre<input name="nombre" /></label>
          <label className="f">Correo<input name="correo" /></label>
          <label className="f">Contraseña inicial<input name="contrasena" type="password" autoComplete="new-password" /></label>
          <label className="f">Rol
            <select name="rol" defaultValue="">
              <option value="">— Seleccione —</option><option value="ESTUDIANTE">Estudiante</option>
              <option value="DOCENTE">Docente</option><option value="ENCARGADO">Encargado de laboratorio</option>
            </select>
          </label>
          <div className="full"><button className="p" type="submit">Crear cuenta</button></div>
        </form>
      </div>
      <div className="card">
        <h2>Usuarios</h2>
        <table>
          <thead><tr><th>#</th><th>Nombre</th><th>Correo</th><th>Rol</th></tr></thead>
          <tbody>
            {usuarios.map((u) => <tr key={u.id}><td>{u.id}</td><td>{u.nombre}</td><td>{u.correo}</td><td>{u.rol.toLowerCase()}</td></tr>)}
          </tbody>
        </table>
      </div>
    </>
  );
}
