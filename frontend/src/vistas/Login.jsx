import React from 'react';
import { api, datosDelFormulario } from '../api.js';

/** HU-09: iniciar sesión. */
export default function Login({ avisar, alIniciar }) {
  const enviar = async (e) => {
    e.preventDefault();
    try {
      const r = await api('POST', 'sesion', datosDelFormulario(e.target));
      alIniciar(r.token, r.usuario);
    } catch (x) { avisar(x.message, false); }
  };
  return (
    <>
      <div className="card login">
        <h2>Iniciar sesión</h2>
        <form id="f" onSubmit={enviar}>
          <label className="f">Correo<input name="correo" autoComplete="username" /></label>
          <label className="f">Contraseña<input name="contrasena" type="password" autoComplete="current-password" /></label>
          <button className="p" type="submit">Ingresar</button>
        </form>
      </div>
      <div className="card login demo">
        <b>Cuentas de demostración</b><br />
        Estudiante: <code>estudiante@itm.edu.co</code> / <code>Estudiante123</code><br />
        Docente: <code>docente@itm.edu.co</code> / <code>Docente123</code><br />
        Encargado: <code>encargado@itm.edu.co</code> / <code>Encargado123</code>
      </div>
    </>
  );
}
