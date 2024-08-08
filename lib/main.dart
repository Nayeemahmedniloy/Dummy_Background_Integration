import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: PaymentScreen(),
    );
  }
}



class PaymentScreen extends StatefulWidget {
  @override
  _PaymentScreenState createState() => _PaymentScreenState();
}

class _PaymentScreenState extends State<PaymentScreen> {
  static const platform = MethodChannel('com.example.untitled3/payment');
  String _result = '';

  Future<void> _performTransaction() async {
    try {
      final result = await platform.invokeMethod<String>('doTransaction', {
        'functionId': '01',
        'amount': '100',
        'appId': 'untitled3',
      });

      if (result != null) {
        setState(() {
          _result = result;  // Use the result only if it's not null
        });
      } else {
        setState(() {
          _result = "Transaction returned null"; // Handle null case
        });
      }
    } on PlatformException catch (e) {
      setState(() {
        _result = "Failed to complete transaction: '${e.message}'.";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Payment Screen'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: _performTransaction,
              child: Text('Perform Transaction'),
            ),
            SizedBox(height: 20),
            Text(_result),
          ],
        ),
      ),
    );
  }
}


class PaymentService {
  static const MethodChannel _channel = MethodChannel('com.example.untitled3/payment');

  static Future<String?> doTransaction({
    required String functionId,
    required String amount,
    required String appId,
  }) async {
    try {
      final String? result = await _channel.invokeMethod('doTransaction', {
        'Function ID': functionId,
        'Amount': amount,
        'APP ID': appId,
      });
      return result;
    } on PlatformException catch (e) {
      print("Failed to complete transaction: '${e.message}'.");
      return null;
    }
  }
}

