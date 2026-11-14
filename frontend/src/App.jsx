import React, { useEffect, useRef, useState } from 'react';
import { alSesionVencida, api, guardarSesion, leerSesion } from './api.js';
import Login from './vistas/Login.jsx';
import Disponibilidad from './vistas/Disponibilidad.jsx';
import Solicitar from './vistas/Solicitar.jsx';
import MisSolicitudes from './vistas/MisSolicitudes.jsx';
import MisPrestamos from './vistas/MisPrestamos.jsx';
import Pendientes from './vistas/Pendientes.jsx';
import Activos from './vistas/Activos.jsx';
import Inventario from './vistas/Inventario.jsx';
import Usuarios from './vistas/Usuarios.jsx';

// Pestañas según el rol (RNF-02: la pantalla solo muestra lo que el rol puede hacer; el servidor lo vuelve a exigir).
const PESTANAS_ENCARGADO = [['pendientes', 'Solicitudes pendientes'], ['activos', 'Préstamos activos'], ['inventario', 'Inventario'], ['usuarios', 'Usuarios']];
const PESTANAS_SOLICITANTE = [['disponibilidad', 'Disponibilidad'], ['solicitar', 'Solicitar préstamo'], ['mias', 'Mis solicitudes'], ['prestamos', 'Mis préstamos']];

export default function App() {
  const [sesion, setSesion] = useState(leerSesion());
  const [vista, setVista] = useState(null);
  const [prefill, setPrefill] = useState({});
  const [aviso, setAviso] = useState(null);
  const temporizador = useRef(null);

  const avisar = (texto, ok = true) => {
    setAviso({ texto, ok });
    clearTimeout(temporizador.current);
    temporizador.current = setTimeout(() => setAviso(null), 5000);
  };
  const cerrarLocal = () => { guardarSesion(null); setSesion({ token: null, actor: null }); setVista(null); setPrefill({}); };
  useEffect(() => { alSesionVencida(cerrarLocal); }, []);

  const iniciar = async (token, actor) => {
    guardarSesion(token, actor); setSesion({ token, actor }); setPrefill({});
    setVista((actor.rol === 'ENCARGADO' ? PESTANAS_ENCARGADO : PESTANAS_SOLICITANTE)[0][0]);
    if (actor.rol === 'ENCARGADO') return;
    // HU-04 / RF-04: al iniciar sesión, si hay avisos nuevos de aprobación o rechazo, se muestran de inmediato
    try {
      const nuevas = (await api('GET', 'notificaciones')).filter((n) => !n.leida).length;
      if (nuevas) {
        setVista('mias');
        avisar(nuevas === 1 ? 'Tiene 1 notificación nueva sobre sus solicitudes' : `Tiene ${nuevas} notificaciones nuevas sobre sus solicitudes`);
      }
    } catch (e) { /* sin avisos: se queda en Disponibilidad */ }
  };
  const salir = async () => { try { await api('DELETE', 'sesion'); } catch (e) { /* ya estaba cerrada */ } cerrarLocal(); };

  const { actor } = sesion;
  const pestanas = !actor ? [] : actor.rol === 'ENCARGADO' ? PESTANAS_ENCARGADO : PESTANAS_SOLICITANTE;
  const actual = vista || (pestanas.length ? pestanas[0][0] : null);
  const irASolicitar = (recursoId) => { setPrefill((p) => ({ ...p, recursoId })); setVista('solicitar'); };

  const pantallas = {
    disponibilidad: <Disponibilidad avisar={avisar} prefill={prefill} setPrefill={setPrefill} irASolicitar={irASolicitar} />,
    solicitar: <Solicitar avisar={avisar} prefill={prefill} limpiar={() => setPrefill({})} />,
    mias: <MisSolicitudes />,
    prestamos: <MisPrestamos />,
    pendientes: <Pendientes avisar={avisar} />,
    activos: <Activos avisar={avisar} />,
    inventario: <Inventario avisar={avisar} />,
    usuarios: <Usuarios avisar={avisar} />,
  };

  return (
    <>
      <header>
        <h1>Sistema de Préstamo de Recursos de Laboratorio</h1>
        <span id="quien">{actor ? `${actor.nombre} · ${actor.rol.toLowerCase()}` : ''}</span>
        {actor && <button className="s" id="salir" onClick={salir}>Cerrar sesión</button>}
      </header>
      {aviso && <div id="msg" style={{ background: aviso.ok ? '#1E7B4A' : '#B3261E' }}>{aviso.texto}</div>}
      <main>
        {!actor ? <Login avisar={avisar} alIniciar={iniciar} /> : (
          <>
            <nav id="tabs">
              {pestanas.map(([clave, texto]) => (
                <button key={clave} className={clave === actual ? 'on' : ''} onClick={() => setVista(clave)}>{texto}</button>
              ))}
            </nav>
            <section id="vista">{pantallas[actual]}</section>
          </>
        )}
      </main>
    </>
  );
}
