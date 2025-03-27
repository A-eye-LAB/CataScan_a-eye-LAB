import 'dart:io';
import 'package:cata_scan_flutter/services/image/image_storage_service.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class PreviewScreen extends StatelessWidget {
  const PreviewScreen({super.key, required this.imagePath});
  static const routeName = '/preview';
  final String imagePath;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Column(
          children: [
            const SizedBox(height: 50),
            Image.file(
              width: double.infinity,
              fit: BoxFit.fitWidth,
              File(imagePath),
            ),
            const SizedBox(height: 50),
            Expanded(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      const result = (false, null);
                      Navigator.pop(context, result);
                    },
                    child: const Text('Re Capture'),
                  ),
                  ElevatedButton(
                    onPressed: () async {
                      final permanentPath = await ImageStorageService.saveImagePermanently(imagePath);

                      final result = (true, permanentPath);

                      if (context.mounted) {
                        context.pop(result);
                      }
                    },
                    child: const Text('Use'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
