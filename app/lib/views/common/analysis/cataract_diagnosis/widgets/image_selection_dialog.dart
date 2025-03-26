import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

class ImageSelectionDialog extends ConsumerWidget {
  const ImageSelectionDialog({
    super.key,
    required this.normalImages,
    required this.cataractImages,
  });

  final List<String> normalImages;
  final List<String> cataractImages;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Dialog(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              "Select Test Image",
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            DefaultTabController(
              length: 2,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const TabBar(
                    tabs: [
                      Tab(text: "Normal Eyes"),
                      Tab(text: "Cataract Eyes"),
                    ],
                  ),
                  SizedBox(
                    width: 600,
                    height: 400,
                    child: TabBarView(
                      children: [
                        // Normal Images Grid
                        _buildImageGrid(context, normalImages),
                        // Cataract Images Grid
                        _buildImageGrid(context, cataractImages),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageGrid(BuildContext context, List<String> images) {
    return GridView.builder(
      padding: const EdgeInsets.all(8),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 4,
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
      ),
      itemCount: images.length,
      itemBuilder: (context, index) {
        return InkWell(
          onTap: () {
            Navigator.of(context).pop(images[index]);
          },
          child: Image.asset(
            images[index],
            fit: BoxFit.cover,
          ),
        );
      },
    );
  }
}
