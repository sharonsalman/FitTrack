const express = require('express');
const admin = require('firebase-admin');
const serviceAccount = require('./fittrack-70436-firebase-adminsdk-q0kkb-0f977c6124.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: 'https://fittrack-70436.firebaseio.com'
});

const app = express();
const port = 3000;

app.use(express.json());

// Root route
app.get('/', (req, res) => {
  res.send('Welcome to FitTrack Server');
});

// Route to save user data
app.post('/saveUserData', async (req, res) => {
  const { email, name, programNumber, weight, age } = req.body;

  try {
    await admin.firestore().collection('users').doc(email).set({
      name,
      programNumber,
      weight,
      age
    });
    res.status(200).send('User data saved successfully');
  } catch (error) {
    console.error('Error saving user data:', error);
    res.status(500).send('Error saving user data');
  }
});

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
