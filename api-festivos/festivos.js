/**
 * Calcula el Domingo de Pascua para un año dado.
 * Fórmula: dias = d + (2b+4c+6d+5) MOD 7
 * Donde: a = año MOD 19, b = año MOD 4, c = año MOD 7, d = (19a+24) MOD 30
 * Domingo de Ramos = 15 marzo + dias, Domingo de Pascua = 7 días después
 */
export function calcularDomingoPascua(anio) {
  const a = anio % 19;
  const b = anio % 4;
  const c = anio % 7;
  const d = (19 * a + 24) % 30;
  const dias = d + (2 * b + 4 * c + 6 * d + 5) % 7;
  // 15 de marzo (mes 2 en JS, día 15) + dias = Domingo de Ramos
  const domingoRamos = new Date(anio, 2, 15 + dias);
  // Domingo de Pascua = Domingo de Ramos + 7 días
  const domingoPascua = new Date(domingoRamos);
  domingoPascua.setDate(domingoPascua.getDate() + 7);
  return domingoPascua;
}

/**
 * Mueve una fecha al siguiente lunes (Ley de Puente Festivo)
 */
function moverAlLunes(fecha) {
  const resultado = new Date(fecha);
  const diaSemana = resultado.getDay(); // 0=Domingo, 1=Lunes...
  if (diaSemana === 0) {
    resultado.setDate(resultado.getDate() + 1);
  } else if (diaSemana !== 1) {
    const diasHastaLunes = 8 - diaSemana;
    resultado.setDate(resultado.getDate() + diasHastaLunes);
  }
  return resultado;
}

function formatearFecha(fecha) {
  const anio = fecha.getFullYear();
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${anio}-${mes}-${dia}`;
}

export async function calcularFestivos(tiposCollection, anio) {
  const tipos = await tiposCollection.find({}).toArray();
  const festivos = [];
  const domingoPascua = calcularDomingoPascua(anio);

  for (const tipo of tipos) {
    for (const festivo of tipo.festivos) {
      let fecha;

      if (tipo.id === 1) {
        // Fijo: fecha directa
        fecha = new Date(anio, festivo.mes - 1, festivo.dia);
      } else if (tipo.id === 2) {
        // Ley de Puente: trasladar al siguiente lunes
        fecha = new Date(anio, festivo.mes - 1, festivo.dia);
        fecha = moverAlLunes(fecha);
      } else if (tipo.id === 3) {
        // Basado en Pascua: sin puente
        fecha = new Date(domingoPascua);
        fecha.setDate(fecha.getDate() + festivo.diasPascua);
      } else if (tipo.id === 4) {
        // Pascua + Puente festivo
        fecha = new Date(domingoPascua);
        fecha.setDate(fecha.getDate() + festivo.diasPascua);
        fecha = moverAlLunes(fecha);
      } else {
        continue;
      }

      festivos.push({
        fecha: formatearFecha(fecha),
        nombre: festivo.nombre,
        dia: fecha.getDate(),
        mes: fecha.getMonth() + 1,
        anio: fecha.getFullYear()
      });
    }
  }

  return festivos.sort((a, b) => a.fecha.localeCompare(b.fecha));
}
