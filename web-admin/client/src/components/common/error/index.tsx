'use client';

import { useEffect } from 'react';
import { AlertCircle } from 'lucide-react';

interface ErrorUIProps {
    error: Error & { digest?: string };
    reset: () => void;
}

function ErrorUI({ error, reset }: ErrorUIProps) {
    useEffect(() => {
        // Log the error to an error reporting service
        console.error(error);
    }, [error]);

    return (
        <div className="flex items-center justify-center bg-gray-100 px-4 py-8 h-[100%]">
            <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-md">
                <div className="flex flex-col items-center text-center">
                    <AlertCircle
                        className="h-12 w-12 text-red-500 mb-4"
                        aria-hidden="true"
                    />
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">
                        An error occurred
                    </h2>
                    <p className="text-gray-600 mb-6">
                        {
                            'Sorry, something went wrong. Please try again. \nCheck console for details.'
                        }
                    </p>
                    <button
                        onClick={() => reset()}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors">
                        Try Again
                    </button>
                </div>
                {error && error.digest && (
                    <p className="mt-4 text-sm text-gray-500 text-center">
                        Error Code:{' '}
                        <code className="font-mono">{error.digest}</code>
                    </p>
                )}
            </div>
        </div>
    );
}

export default ErrorUI;
