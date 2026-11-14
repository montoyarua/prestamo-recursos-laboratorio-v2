// Cliente de la API REST. El token de sesión se envía en «Authorization: Bearer» en cada petición.
let alVencerSesion = () => {};
export function alSesionVencida(f) { alVencerSesion = f; }

export function leerSesion() {
  try { return { token: sessionStorage.getItem('token'), actor: JSON.parse(sessionStorage.getItem('actor') || 'null') }; }
  catch (e) { return { token: null, actor: null }; }
}
export function guardarSesion(token, actor) {
  try {
    if (token) { sessionStorage.setItem('token', token); sessionStorage.setItem('actor', JSON.stringify(actor)); }
    else sessionStorage.clear();
  } catch (e) { /* sin almacenamiento: la sesión solo dura mientras la página siga abierta */ }
}

export async function api(metodo, ruta, cuerpo) {
  const { token } = leerSesion();
  const cabeceras = { 'Content-Type': 'application/json' };
  if (token) cabeceras.Authorization = 'Bearer ' + token;
  const res = await fetch('/api/' + ruta, { method: metodo, headers: cabeceras, body: cuerpo ? JSON.stringify(cuerpo) : undefined });
  let datos = {};
  try { datos = await res.json(); } catch (e) { /* respuesta sin cuerpo */ }
  if (res.status === 401 && ruta !== 'sesion') { alVencerSesion(); throw new Error(datos.error || 'Sesión vencida'); }
  if (!res.ok) throw new Error(datos.error || 'Error');
  return datos;
}

export const hoy = () => { const d = new Date(); return new Date(d - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10); };
export const datosDelFormulario = (form) => Object.fromEntries(new FormData(form));
