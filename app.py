from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model
from PIL import Image
import numpy as np
import io
import base64

app = Flask(__name__)

# Modeli yükle (model dosyanın adı neyse onu yaz)
model = load_model('skin_type_model.h5')

# Resmi modele uygun şekilde hazırla
def preprocess_image(image_bytes):
    img = Image.open(io.BytesIO(image_bytes)).convert('RGB')
    img = img.resize((224, 224))  # Modelin giriş boyutuna göre değiştir
    arr = np.array(img) / 255.0
    arr = np.expand_dims(arr, axis=0)
    return arr

# /predict endpoint'i
@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        img_b64 = data['image']
        img_bytes = base64.b64decode(img_b64.split(',')[1])
        arr = preprocess_image(img_bytes)
        pred = model.predict(arr)
        label = int(np.argmax(pred))
        # Geçici label_map
        label_map = {
            0: {"name": "akiec", "desc": "Aktinik keratoz ve intraepitelyal karsinoma"},
            1: {"name": "bcc", "desc": "Bazal hücreli karsinom"},
            2: {"name": "bkl", "desc": "Benign keratoz benzeri lezyonlar"},
            3: {"name": "df", "desc": "Dermatofibroma"},
            4: {"name": "mel", "desc": "Melanom"},
            5: {"name": "nv", "desc": "Melanositik nevüs"},
            6: {"name": "vasc", "desc": "Vasküler lezyonlar"}
        }
        if label not in label_map:
            return jsonify({'error': f'Bilinmeyen sınıf: {label}', 'probs': pred.tolist()})
        return jsonify({
            'diagnosis': label_map[label]["name"],
            'description': label_map[label]["desc"],
            'probs': pred.tolist()
        })
    except Exception as e:
        import traceback
        print(traceback.format_exc())
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(port=5001)