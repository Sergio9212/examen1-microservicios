import express from 'express';
import { MongoClient } from 'mongodb';
import { calcularFestivos } from './festivos.js';

const app = express();
const PORT = process.env.PORT || 3000;

const mongoUrl = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const dbName = 'festivos';
const collectionName = 'tipos';

let db;

async function conectarMongoDB() {
  try {
    const client = await MongoClient.connect(mongoUrl);
    db = client.db(dbName);
    console.log('Conectado a MongoDB');
  } catch (err) {
    console.error('Error conectando a MongoDB:', err);
    process.exit(1);
  }
}

// Validar si una fecha es festiva
app.get('/api/festivos/verificar/:anio/:mes/:dia', async (req, res) => {
  try {
    const anio = parseInt(req.params.anio, 10);
    const mes = parseInt(req.params.mes, 10);
    const dia = parseInt(req.params.dia, 10);

    // Validar que la fecha sea válida
    const fecha = new Date(anio, mes - 1, dia);
    if (fecha.getFullYear() !== anio || fecha.getMonth() !== mes - 1 || fecha.getDate() !== dia) {
      return res.status(400).json({
        error: 'Fecha no válida',
        mensaje: `La fecha ${dia}/${mes}/${anio} no es una fecha válida`
      });
    }

    const festivos = await calcularFestivos(db.collection(collectionName), anio);
    const fechaStr = `${anio}-${String(mes).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
    const festivo = festivos.find(f => f.fecha === fechaStr);

    if (festivo) {
      res.json({
        festivo: true,
        fecha: fechaStr,
        nombre: festivo.nombre
      });
    } else {
      res.json({
        festivo: false,
        fecha: fechaStr,
        nombre: 'No es festivo'
      });
    }
  } catch (err) {
    console.error('Error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// Listar festivos de un año (para la API Springboot)
app.get('/api/festivos/listar/:anio', async (req, res) => {
  try {
    const anio = parseInt(req.params.anio, 10);
    if (isNaN(anio) || anio < 1900 || anio > 2100) {
      return res.status(400).json({ error: 'Año no válido' });
    }

    const festivos = await calcularFestivos(db.collection(collectionName), anio);
    res.json(festivos);
  } catch (err) {
    console.error('Error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

conectarMongoDB().then(() => {
  app.listen(PORT, () => {
    console.log(`API Festivos escuchando en http://localhost:${PORT}`);
  });
});
